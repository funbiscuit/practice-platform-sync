package org.CliSystem;

import picocli.CommandLine;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@CommandLine.Command(name = "CommandApi", mixinStandardHelpOptions = true)
public class CommandApi implements Callable<String> {

    @CommandLine.Option(names = {"--target-url", "-t"}, description = "request to url")
    String url = "http://localhost:8080/";

    @CommandLine.Option(names = {"--source-dir", "-s"}, description = "path to directory")
    String path = "D:/test-pkg";


    @Override
    public String call() {
        RemoteModuleService remoteModuleService = new RemoteModuleService(url);
        LocalModuleService localModuleService = new LocalModuleService();
        List<ModuleObj> localModules = localModuleService.parseModules(path);
        List<ModuleDto> remoteModules = remoteModuleService.getModules();
        deleteNotInLocal(remoteModules, localModules, remoteModuleService);
        updateNotInLocal(remoteModules, localModules, remoteModuleService);
        List<ModuleObj> moduleObjs = filterNotInLocal(remoteModules, localModules);
        moduleObjs.forEach(remoteModuleService::save);
        return "Всё успешно!";
    }

    public List<ModuleObj> filterNotInLocal(List<ModuleDto> moduleDtos, List<ModuleObj> moduleObjs) {

        if (moduleDtos.isEmpty()) {
            return moduleObjs;
        }
      
        Set<String> dtoNames = moduleDtos.stream()
                .map(ModuleDto::name)
                .collect(Collectors.toSet());

        return moduleObjs.stream()
                .filter(moduleObj -> !dtoNames.contains(moduleObj.name()))
                .collect(Collectors.toList());
    }

    public void deleteNotInLocal(List<ModuleDto> moduleDtos, List<ModuleObj> moduleObjs, RemoteModuleService remoteModuleService) {
        if (moduleDtos.isEmpty()) {
            return;
        }
        Set<String> objNames = moduleObjs.stream()
                .map(ModuleObj::name)
                .collect(Collectors.toSet());
        List<ModuleDto> deleteModules = moduleDtos.stream()
                .filter(moduleDto -> !objNames.contains(moduleDto.name()))
                .toList();

        deleteModules.forEach(moduleDto -> remoteModuleService.delete(moduleDto.name()));
    }

    public void updateNotInLocal(List<ModuleDto> moduleDtos, List<ModuleObj> moduleObjs, RemoteModuleService remoteModuleService) {
        if (moduleDtos.isEmpty()) {
            return;
        }
        List<ModuleObj> matchingModules = moduleObjs.stream()
                .filter(obj -> moduleDtos.stream().noneMatch(dto -> dto.metadata().get("CheckSum").equals(obj.metadata().get("CheckSum"))))
                .toList();
        matchingModules.forEach(moduleObj -> remoteModuleService.update(moduleObj.name(), moduleObj));
    }

}
