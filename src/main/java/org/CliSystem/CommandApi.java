package org.CliSystem;

import org.CliSystem.Service.GitService;
import org.CliSystem.Service.LocalModuleService;
import org.CliSystem.Service.RemoteModuleService;
import org.apache.commons.collections4.SetUtils;
import picocli.CommandLine;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "CommandApi", mixinStandardHelpOptions = true)
public class CommandApi implements Callable<String> {

    @CommandLine.Option(names = {"--target-url", "-t"}, description = "request to url")
    String url = "http://localhost:8080/";

    @CommandLine.Option(names = {"--source-dir", "-d"}, description = "path to directory")
    String path = "D:/test-py";

    @CommandLine.Option(names = {"--source-git", "-g"}, description = "request to url")
    String gitUrl = "https://github.com/funbiscuit/practice-test-pkg.git";

    @CommandLine.Option(names = {"--source-branch", "-b"}, description = "request to url")
    String branch = "main";


    @Override
    public String call() {
        GitService gitService = new GitService();
        RemoteModuleService remoteModuleService = new RemoteModuleService(url);
        LocalModuleService localModuleService = new LocalModuleService();
        Map<String, ModuleObj> gitModules = gitService.cloneRepo(gitUrl, branch, path);
        Map<String, ModuleObj> localModules = localModuleService.parseModules(path);
        localModules.putAll(gitModules);
        Map<String, ModuleDto> remoteModules = remoteModuleService.getModules();
        deleteNotInLocal(remoteModules, localModules, remoteModuleService);
        updateChanged(remoteModules, localModules, remoteModuleService);
        saveNew(remoteModules, localModules, remoteModuleService);
        return "Всё успешно!";
    }

    public void deleteNotInLocal(Map<String, ModuleDto> remoteModules, Map<String, ModuleObj> localModules, RemoteModuleService remoteModuleService) {
        Set<String> deleteModules = SetUtils.difference(remoteModules.keySet(), localModules.keySet());
        deleteModules.forEach(remoteModuleService::delete);
    }

    public void updateChanged(Map<String, ModuleDto> remoteModules, Map<String, ModuleObj> localModules, RemoteModuleService remoteModuleService) {
        Set<String> commonModules = SetUtils.intersection(remoteModules.keySet(), localModules.keySet());
        commonModules.stream()
                .map(localModules::get)
                .filter(module -> !remoteModules.get(module.name()).getCheckSum()
                        .equals(localModules.get(module.name()).getCheckSum()))
                .forEach(moduleObj -> remoteModuleService.update(moduleObj.name(), moduleObj));
    }

    public void saveNew(Map<String, ModuleDto> remoteModules, Map<String, ModuleObj> localModules, RemoteModuleService remoteModuleService) {
        Set<String> differenceModule = SetUtils.difference(localModules.keySet(), remoteModules.keySet());
        differenceModule.stream()
                .map(localModules::get)
                .forEach(remoteModuleService::save);
    }

}
