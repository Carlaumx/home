package dev.carlaum.home.command;

import dev.carlaum.home.HomePlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class CommandBase extends Command {

    public CommandBase(@NotNull String name) {
        super(name);
    }

    public abstract void run(CommandSender sender, String[] args);

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cEste comando deve ser executado por um Player");
            return true;
        }
        Bukkit.getScheduler().runTaskAsynchronously(HomePlugin.INSTANCE, () -> run(sender, args));
        return false;
    }
}
