package dev.thezexquex.commandchain.command;

import cloud.commandframework.CommandManager;
import dev.thezexquex.commandchain.CommandChainPlugin;
import org.bukkit.command.CommandSender;

public abstract class CommandBase {

    protected final CommandChainPlugin commandChainPlugin;
    public CommandBase(CommandChainPlugin commandChainPlugin) {
        this.commandChainPlugin = commandChainPlugin;
    }

    public abstract void register(CommandManager<CommandSender> commandManager);
}
