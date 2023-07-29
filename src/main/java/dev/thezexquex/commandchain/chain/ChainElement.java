package dev.thezexquex.commandchain.chain;

import dev.thezexquex.commandchain.CommandChainPlugin;
import org.bukkit.command.CommandSender;

public class ChainElement {
    private CommandElement current;
    private ChainElement next;

    private final CommandChainPlugin commandChainPlugin;

    public ChainElement(CommandChainPlugin commandChainPlugin, CommandElement commandElement) {
        this.current = commandElement;
        this.commandChainPlugin = commandChainPlugin;
    }

    public void setNext(ChainElement next) {
        this.next = next;
    }

    public void execute(CommandSender commandSender, int delay) {
        current.execute(commandSender);
        if (next != null) {
            executeNext(commandSender, delay);
        }
    }

    private void executeNext(CommandSender commandSender, int delay) {
        commandChainPlugin.getServer().getScheduler().runTaskLater(commandChainPlugin, () -> next.execute(commandSender, delay), delay);
    }
}
