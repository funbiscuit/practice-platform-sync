package org.CliSystem.Cli;

import picocli.CommandLine;

@CommandLine.Command(name = "", mixinStandardHelpOptions = true, subcommands = {DiffCli.class, DeployCli.class})
public class MainCli {
}
