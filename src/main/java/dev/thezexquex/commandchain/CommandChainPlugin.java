package dev.thezexquex.commandchain;

import cloud.commandframework.CommandManager;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import dev.thezexquex.commandchain.command.ChainCommand;
import dev.thezexquex.commandchain.command.ReloadCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.NodePath;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.util.function.Function;
public final class CommandChainPlugin extends JavaPlugin {
    private YamlConfigurationLoader yamlConfigurationLoader;
    private ConfigurationNode rootNode;
    private MiniMessage miniMessage;
    @Override
    public void onEnable() {
        saveResource("config.yml", true);

        CommandManager<CommandSender> commandManager;

        try {
             commandManager = new PaperCommandManager<>(
                    this,
                    CommandExecutionCoordinator.simpleCoordinator(),
                    Function.identity(),
                    Function.identity()
            );
        } catch (Exception e) {
            getLogger().severe("Failed to instantiate command manager. Disabling...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        var path = getDataFolder().toPath().resolve("config.yml");
        yamlConfigurationLoader = YamlConfigurationLoader.builder().path(path).build();

        reload();

        miniMessage = MiniMessage.builder().tags(StandardTags.defaults()).build();

        new ReloadCommand(this).register(commandManager);
        new ChainCommand(this).register(commandManager);
    }

    public Component getPrefix() {
        var prefix = rootNode.node(NodePath.path("messages", "prefix")).getString();
        return prefix != null ? miniMessage.deserialize(prefix) : Component.text("N/A: messages.prefix");
    }

    public void reload() {
        try {
            rootNode = yamlConfigurationLoader.load();
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }
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
