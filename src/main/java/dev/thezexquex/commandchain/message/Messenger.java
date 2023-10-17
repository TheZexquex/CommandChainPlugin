package dev.thezexquex.commandchain.message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.bukkit.command.CommandSender;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.NodePath;

public class Messenger {

    private MiniMessage miniMessage;
    private final ConfigurationNode rootNode;

    public Messenger(ConfigurationNode rootNode) {
        this.rootNode = rootNode;
        this.miniMessage = miniMessage = MiniMessage.builder().tags(StandardTags.defaults()).build();
    }

    private Component getPrefix() {
        var prefix = rootNode.node(NodePath.path("messages", "prefix")).getString();
        return prefix != null ? miniMessage.deserialize(prefix) : Component.text("N/A: messages.prefix");
    }

    public void sendMessage(CommandSender sender, NodePath nodePath, TagResolver... tagResolvers) {
        var message = (String) rootNode.node(nodePath).getString();
        if (message == null) {
            message = "N/A: " + nodePath.toString();
        }
        var component = miniMessage.deserialize(message, tagResolvers);
        var result = getPrefix().append(component);
        sender.sendMessage(result);
    }
}
