package org.CliSystem.Cli;

import org.CliSystem.ModuleDto;
import org.CliSystem.ModuleObj;
import org.CliSystem.OptionDto;
import org.CliSystem.Service.RemoteModuleService;
import org.apache.commons.collections4.SetUtils;
import picocli.CommandLine;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

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
        OptionDto optionDto = new OptionDto((yaml != null) ? deployCli.parseYaml(yaml).target().url() : url,
                path, gitUrl, yaml, branch);
        Map<String, ModuleObj> localModules = deployCli.getLocalModules(optionDto);
        RemoteModuleService remoteModuleService = new RemoteModuleService(optionDto.url());
        Map<String, ModuleDto> remoteModules = remoteModuleService.getModules();
        Map<String, Set<String>> diff = new HashMap<>();
        diff.put("New modules:", SetUtils.difference(localModules.keySet(), remoteModules.keySet()));
        diff.put("Orphaned modules:", SetUtils.difference(remoteModules.keySet(), localModules.keySet()));
        Set<String> commonModules = SetUtils.intersection(remoteModules.keySet(), localModules.keySet());
        if (!commonModules.isEmpty()) {
            diff.put("Changed modules:", commonModules.stream()
                    .map(key -> localModules.get(key).name())
                    .filter(module -> !remoteModules.get(module).getCheckSum()
                            .equals(localModules.get(module).getCheckSum()))
                    .collect(Collectors.toSet()));
        }
        for (String name : diff.keySet()) {
            if (!diff.get(name).isEmpty()) {
                System.out.println(name);
                diff.get(name).forEach(s -> System.out.println("- " + s));
            }
        }
        return "";
    }
}

