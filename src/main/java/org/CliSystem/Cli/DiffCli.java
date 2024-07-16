package org.CliSystem.Cli;

import org.CliSystem.ModuleDto;
import org.CliSystem.ModuleObj;
import org.CliSystem.Service.GitService;
import org.CliSystem.Service.LocalModuleService;
import org.CliSystem.Service.RemoteModuleService;
import org.CliSystem.Yaml.Package;
import org.CliSystem.Yaml.Ref;
import org.CliSystem.Yaml.YamlDto;
import org.apache.commons.collections4.SetUtils;
import picocli.CommandLine;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "diff", mixinStandardHelpOptions = true)
public class DiffCli implements Callable<String> {

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
        DeployCli deployCli = new DeployCli();
        Map<String, ModuleObj> localModules = getLocalModules();
        RemoteModuleService remoteModuleService = new RemoteModuleService(url);
        Map<String, ModuleDto> remoteModules = remoteModuleService.getModules();
        Set<String> diffLocal = SetUtils.difference(localModules.keySet(), remoteModules.keySet());
        Set<String> diffRemote = SetUtils.difference(remoteModules.keySet(), localModules.keySet());
        Set<String> commonModules = SetUtils.intersection(remoteModules.keySet(), localModules.keySet());
        if (!diffLocal.isEmpty()) {
            System.out.println("New modules:");
            diffLocal.forEach(module -> System.out.println("- " + module));
        }
        if (!commonModules.isEmpty()) {
            System.out.println("Changed modules:");
            commonModules.stream()
                    .map(localModules::get)
                    .filter(module -> !remoteModules.get(module.name()).getCheckSum()
                            .equals(localModules.get(module.name()).getCheckSum()))
                    .forEach(moduleObj -> System.out.println("- " + moduleObj.name()));
        }
        if (!diffRemote.isEmpty()) {
            System.out.println("Orphaned modules:");
            diffRemote.forEach(module -> System.out.println("- " + module));
        }
        return "";
    }

    public Map<String, ModuleObj> getLocalModules() {
        if (yaml != null) {
            GitService gitService = new GitService();
            DeployCli deployCli = new DeployCli();
            YamlDto yamlDto = deployCli.parseYaml(yaml);
            url = yamlDto.target().url();
            Map<String, ModuleObj> allModules = new HashMap<>();
            Map<String, ModuleObj> packageModules;
            for (Package pac : yamlDto.packages()) {
                packageModules = gitService.parseRepo(pac);
                Set<String> as = packageModules.keySet();
                for (String name : as) {
                    packageModules.get(name).metadata().put("package-name", pac.name());
                    packageModules.get(name).metadata().put("package-ref-branch", pac.ref().branch());
                }
                allModules.putAll(packageModules);
            }
            return allModules;
        } else if (path != null && gitUrl == null) {
            return new LocalModuleService().parseModules(path);
        } else if (path == null && gitUrl != null) {
            return new GitService().parseRepo(new Package(gitUrl, new Ref(branch), null));
        }
        throw new RuntimeException("Incorrect input of the module source!");
    }
}
