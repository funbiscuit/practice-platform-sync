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
        Map<String, ModuleObj> localModules = localModuleService.parseModules(path);
        Map<String, ModuleDto> remoteModules = remoteModuleService.getModules();
        deleteNotInLocal(remoteModules, localModules, remoteModuleService);
        List<ModuleObj> moduleObjs = saveNew(remoteModules, localModules, remoteModuleService);
        moduleObjs.forEach(remoteModuleService::save);
        return "Всё успешно!";
    }

    public void deleteNotInLocal(Map<String, ModuleDto> remoteModules, Map<String, ModuleObj> localModules, RemoteModuleService remoteModuleService) {
        if (remoteModules.isEmpty()) {
            return;
        }
        Set<String> deleteModules = SetUtils.difference(remoteModules.keySet(), localModules.keySet());
        deleteModules.forEach(remoteModuleService::delete);
    }

    public List<ModuleObj> saveNew(Map<String, ModuleDto> remoteModules, Map<String, ModuleObj> localModules, RemoteModuleService remoteModuleService) {
        if (remoteModules.isEmpty()) {
            return null;
        }
        Set<String> commonModules = SetUtils.intersection(remoteModules.keySet(),localModules.keySet());
        commonModules.stream()
                .map(localModules::get)
                .filter(module -> !remoteModules.get(module.name()).getCheckSum()
                                    .equals(localModules.get(module.name()).getCheckSum()))
                .forEach(moduleObj -> remoteModuleService.update(moduleObj.name(), moduleObj));

        Set<String> dtoNames = remoteModules.keySet();
        List<ModuleObj> objNames = localModules.values().stream().toList();

        return objNames.stream()
                .filter(localModule -> !dtoNames.contains(localModule.name()))
                .collect(Collectors.toList());
    }

}
