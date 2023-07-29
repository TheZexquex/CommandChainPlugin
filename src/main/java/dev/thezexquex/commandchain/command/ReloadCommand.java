package dev.thezexquex.commandchain.command;

import cloud.commandframework.CommandManager;
import cloud.commandframework.context.CommandContext;
import dev.thezexquex.commandchain.CommandChainPlugin;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.configurate.NodePath;

public class ReloadCommand extends CommandBase {

    public ReloadCommand(CommandChainPlugin commandChainPlugin) {
        super(commandChainPlugin);
    }

    @Override
    public void register(CommandManager<CommandSender> commandManager) {
        commandManager.command(
                commandManager.commandBuilder("commandchain")
                        .literal("reload")
                        .permission("commandchain.command.reload")
                        .handler(this::handelReload)
        );
    }

    private void handelReload(@NonNull CommandContext<CommandSender> commandContext) {
        var sender = commandContext.getSender();

        commandChainPlugin.reload();
        commandChainPlugin.sendMessage(sender, NodePath.path("messages", "reload"));
    }
}
