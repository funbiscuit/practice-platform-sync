package org.CliSystem;

import org.CliSystem.Service.GitService;
import org.CliSystem.Service.LocalModuleService;
import org.CliSystem.Service.RemoteModuleService;
import org.CliSystem.Yaml.Packages;
import org.CliSystem.Yaml.YamlDto;
import org.apache.commons.collections4.SetUtils;
import picocli.CommandLine;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "CommandApi", mixinStandardHelpOptions = true)
public class CommandApi implements Callable<String> {

    @CommandLine.Option(names = {"--target-url", "-t"}, description = "request to url")
    String url;

    @CommandLine.Option(names = {"--source-dir", "-d"}, description = "source modules local folder)")
    String path;

    @CommandLine.Option(names = {"--source-git", "-g"}, description = "source modules git")
    String gitUrl;

    @CommandLine.Option(names = {"--deployment"}, description = "source modules yaml")
    String yaml;

    @CommandLine.Option(names = {"--source-branch", "-b"}, description = "request to url")
    String branch;


    @Override
    public String call() {
        Map<String, ModuleObj> localModules = getLocalModules();
        RemoteModuleService remoteModuleService = new RemoteModuleService(url);
        Map<String, ModuleDto> remoteModules = remoteModuleService.getModules();
        deleteNotInLocal(remoteModules, localModules, remoteModuleService);
        updateChanged(remoteModules, localModules, remoteModuleService);
        saveNew(remoteModules, localModules, remoteModuleService);
        return "Всё успешно!";
    }

    private Map<String, ModuleObj> getLocalModules() {
        if (yaml != null) {
            GitService gitService = new GitService();
            YamlDto yamlDto = gitService.parseYaml(yaml);
            url = yamlDto.target().url();
            Map<String, ModuleObj> gitModules = new HashMap<>();
            Map<String, ModuleObj> package_;
            for (Packages packages : yamlDto.packages()) {
                package_ = gitService.cloneRepo(packages.name(), packages.ref().branch());
                Set<String> as = package_.keySet();
                for (String name : as) {
                    package_.get(name).metadata().put("package-name", packages.name());
                    package_.get(name).metadata().put("package-ref-branch", packages.ref().branch());
                }
                gitModules.putAll(package_);
            }
            return gitModules;
        } else if (path != null && gitUrl == null) {
            return new LocalModuleService().parseModules(path);
        } else if (path == null && gitUrl != null) {
            return new GitService().cloneRepo(gitUrl, branch);
        }
        throw new RuntimeException("Incorrect input of the module source!");
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
