package dev.thezexquex.commandchain;

import cloud.commandframework.CloudCapability;
import cloud.commandframework.CommandManager;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import dev.thezexquex.commandchain.command.ChainCommand;
import dev.thezexquex.commandchain.command.ReloadCommand;
import dev.thezexquex.commandchain.message.Messenger;
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

import java.nio.file.Files;
import java.util.function.Function;
import java.util.logging.Level;

public final class CommandChainPlugin extends JavaPlugin {
    private Configuration configuration;
    private Messenger messenger;

    @Override
    public void onEnable() {

        reload();

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

        if (configuration.useFlagForDelay()) {
            commandManager.setSetting(CommandManager.ManagerSettings.LIBERAL_FLAG_PARSING, true);
        }

        new ReloadCommand(this).register(commandManager);
        new ChainCommand(this).register(commandManager);
    }



    public void reload() {
        var path = getDataFolder().toPath().resolve("config.yml");

        if (!Files.exists(path)) {
            saveResource("config.yml", true);
        }

        var yamlConfigurationLoader = YamlConfigurationLoader.builder().path(path).build();

        ConfigurationNode rootNode;

        try {
            rootNode = yamlConfigurationLoader.load();
        } catch (ConfigurateException e) {
            getLogger().log(Level.SEVERE, "Failed to load configuration...", e);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.messenger = new Messenger(rootNode);

        var maxCommands = rootNode.node(NodePath.path("config", "general", "max-commands")).getInt();
        var useFlagForDelay = rootNode.node(NodePath.path("config", "experimental", "use-flag-for-delay")).getBoolean();

        this.configuration = new Configuration(
                maxCommands, useFlagForDelay
        );
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public Messenger getMessenger() {
        return messenger;
    }
}
