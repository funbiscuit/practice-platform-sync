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
}
