package org.CliSystem;

import org.CliSystem.Cli.MainCli;
import picocli.CommandLine;

public class Main {
    public static void main(String[] args) {
        final CommandLine cmd = new CommandLine(new MainCli());
        int exitCode = cmd.execute(args);
        System.exit(exitCode);
    }
}
