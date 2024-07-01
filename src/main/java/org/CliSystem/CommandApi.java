package org.CliSystem;

import picocli.CommandLine;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "CommandApi",mixinStandardHelpOptions = true)
public class CommandApi implements Callable<String> {

    @CommandLine.Option(names = {"--target-url","-tu"}, description = "request to url")
    String url;

    @CommandLine.Option(names = {"--source-dir","-sd"}, description = "path to directory")
    String path;


    @Override
    public String call() {
        ApiService apiService = new ApiService();
        String text = apiService.save(url,path);
        System.out.println(text);
        return text;
    }
}
