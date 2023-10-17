package dev.thezexquex.commandchain.command;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import dev.thezexquex.commandchain.CommandChainPlugin;
import dev.thezexquex.commandchain.chain.ChainElement;
import dev.thezexquex.commandchain.chain.CommandElement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.configurate.NodePath;

import java.util.*;

public class ChainCommand extends CommandBase {

    public ChainCommand(CommandChainPlugin commandChainPlugin) {
        super(commandChainPlugin);
    }

    public void register(CommandManager<CommandSender> commandManager) {

        Command.Builder<CommandSender> commandBuilder;
        if (commandChainPlugin.getConfiguration().useFlagForDelay()) {
            commandBuilder = commandManager.commandBuilder("chain")
                    .permission("commandchain.command.chain")
                    .flag(commandManager.flagBuilder("delay").withArgument(IntegerArgument.of("delay")))
                    .argument(StringArgument.of("cmd1", StringArgument.StringMode.QUOTED))
                    .argument(StringArgument.of("cmd2", StringArgument.StringMode.QUOTED));
        } else {
            commandBuilder = commandManager.commandBuilder("chain")
                    .permission("commandchain.command.chain")
                    .argument(IntegerArgument.of("delay"))
                    .argument(StringArgument.of("cmd1", StringArgument.StringMode.QUOTED))
                    .argument(StringArgument.of("cmd2", StringArgument.StringMode.QUOTED));
        }

        for (int i = 3; i <= commandChainPlugin.getConfiguration().maxCommands(); i++) {
            commandBuilder = commandBuilder.argument(StringArgument.optional("cmd" + i, StringArgument.StringMode.QUOTED));
        }

        commandBuilder = commandBuilder.handler(this::handleChain);

        commandManager.command(commandBuilder);
    }

    private void handleChain(@NonNull CommandContext<CommandSender> commandContext) {
        var sender = commandContext.getSender();


        var delayInTicks = (int) (commandChainPlugin.getConfiguration().useFlagForDelay() ?
                (commandContext.flags().getValue("delay").isPresent() ?
                        commandContext.flags().getValue("delay").get() : 0)
                : commandContext.get("delay"));

        var cmds = new ArrayList<String>();
        for (int i = 1; i <= commandChainPlugin.getConfiguration().maxCommands(); i++) {
            if (!commandContext.contains("cmd" + i)) {
                break;
            }
            cmds.add(commandContext.get("cmd" + i));
        }

        commandChainPlugin.getMessenger().sendMessage(sender, NodePath.path("messages", "execute-all-header"));
        cmds.forEach(cmd ->
                commandChainPlugin.getMessenger().sendMessage(
                sender, NodePath.path("messages", "execute-all-command"),
                        TagResolver.resolver("command", Tag.inserting(Component.text(cmd))
                ))
        );

        runCommandsWithDelay(delayInTicks, sender, cmds);
    }

    private void runCommandsWithDelay(int delay, CommandSender sender, List<String> commands) {
        ChainElement start = null;
        ChainElement chainElement = null;
        for (String command : commands) {
            if (chainElement == null) {
                chainElement = new ChainElement(commandChainPlugin, new CommandElement(commandChainPlugin, command));
                start = chainElement;
            } else {
                var current = new ChainElement(commandChainPlugin, new CommandElement(commandChainPlugin, command));
                chainElement.setNext(current);
                chainElement = current;
            }
        }
        if (start != null) {
            start.execute(sender, delay);
        }
    }
}
