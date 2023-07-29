package dev.thezexquex.commandchain.chain;

import dev.thezexquex.commandchain.CommandChainPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.spongepowered.configurate.NodePath;

public class CommandElement {

    private final String command;
    private final CommandChainPlugin commandChainPlugin;

    public CommandElement(CommandChainPlugin commandChainPlugin, String command) {
        this.command = command;
        this.commandChainPlugin = commandChainPlugin;
    }

    public void execute(CommandSender sender) {
        commandChainPlugin.sendMessage(sender, NodePath.path(
                "messages", "execute"), TagResolver.resolver("command",
                Tag.inserting(Component.text(command)))
        );
        commandChainPlugin.getServer().dispatchCommand(sender, command);
    }
}
