package com.fortify.cli.ssc.auth_entity.cli.cmd;

import picocli.CommandLine.Command;

@Command(
        name = "user",
        subcommands = {
                SSCAuthEntityDeleteCommand.class,
                SSCAuthEntityListCommand.class
        }
)
public class SSCAuthEntityCommands {
}
