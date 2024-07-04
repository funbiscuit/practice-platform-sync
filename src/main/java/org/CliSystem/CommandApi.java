package org.CliSystem;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.SetUtils;
import picocli.CommandLine;

import java.util.*;
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
        HashMap<String, ModuleObj> localModules = localModuleService.parseModules(path);
        HashMap<String, ModuleDto> remoteModules = remoteModuleService.getModules();
        deleteNotInLocal(remoteModules, localModules, remoteModuleService);
        updateChanged(remoteModules, localModules, remoteModuleService);
        List<ModuleObj> moduleObjs = filterNotInLocal(remoteModules, localModules);
        moduleObjs.forEach(remoteModuleService::save);
        return "Всё успешно!";
    }

    public List<ModuleObj> filterNotInLocal(HashMap<String, ModuleDto> remoteModules, HashMap<String, ModuleObj> localModules) {

        if (remoteModules.isEmpty()) {
            return localModules.values().stream().toList();
        }
        Set<String> dtoNames = remoteModules.keySet();
        List<ModuleObj> objNames = localModules.values().stream().toList();

        return objNames.stream()
                .filter(localModule -> !dtoNames.contains(localModule.name()))
                .collect(Collectors.toList());
    }

    public void deleteNotInLocal(HashMap<String, ModuleDto> remoteModules, HashMap<String, ModuleObj> localModules, RemoteModuleService remoteModuleService) {
        if (remoteModules.isEmpty()) {
            return;
        }
        Set<String> deleteModules = SetUtils.difference(remoteModules.keySet(), localModules.keySet());
        deleteModules.forEach(remoteModuleService::delete);
    }

    public void updateChanged(HashMap<String, ModuleDto> remoteModules, HashMap<String, ModuleObj> localModules, RemoteModuleService remoteModuleService) {
        if (remoteModules.isEmpty()) {
            return;
        }
        Set<String> commonModules = SetUtils.intersection(remoteModules.keySet(),localModules.keySet());
        List<String> matchingModules = commonModules.stream()
                .filter(module -> !remoteModules.get(module).getCheckSum()
                                    .equals(localModules.get(module).getCheckSum()))
                .toList();
        List<ModuleObj> updateModules = matchingModules.stream().map(localModules::get).toList();
        updateModules.forEach(moduleObj -> remoteModuleService.update(moduleObj.name(), moduleObj));
    }

}
