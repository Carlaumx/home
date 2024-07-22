package dev.carlaum.home.command;

import dev.carlaum.home.dao.HomeDAO;
import dev.carlaum.home.enums.Type;
import dev.carlaum.home.model.Home;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class HomesCommand extends CommandBase {

    private final HomeDAO homeDAO;

    public HomesCommand(HomeDAO homeDAO) {
        super("homes");
        this.homeDAO = homeDAO;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        List<Home> homesById = homeDAO.findHomesByPlayerId(player.getUniqueId());

        if(homesById.isEmpty()) {
            player.sendMessage("§cVocê não tem homes definidas.");
            return;
        }

        player.sendMessage("§eHomes: " + homesById.stream().map(home -> home.getType() == Type.PUBLIC ? "§a" + home.getName() : "§f" + home.getName()).collect(Collectors.joining("§7, ")) + "§7.");

    }
}
