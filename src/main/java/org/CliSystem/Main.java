package org.CliSystem;

import picocli.CommandLine;

public class Main {
    public static void main(String[] args) {
        final CommandLine cmd = new CommandLine(new CommandApi());
        int exitCode = cmd.execute(args);
        System.exit(exitCode);
    }
}
