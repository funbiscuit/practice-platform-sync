package org.CliSystem;

import picocli.CommandLine;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "CommandApi",mixinStandardHelpOptions = true)
public class CommandApi implements Callable<String> {

    @CommandLine.Option(names = {"--target-url","-t"}, description = "request to url")
    String url;

    @CommandLine.Option(names = {"--source-dir","-s"}, description = "path to directory")
    String path;


    @Override
    public String call() {
        RemoteModuleService remoteModuleService = new RemoteModuleService(url);
        remoteModuleService.saveAllNewModules(path);
        return "Всё успешно!";
    }
}
