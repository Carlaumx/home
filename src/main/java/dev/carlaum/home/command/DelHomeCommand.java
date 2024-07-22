package dev.carlaum.home.command;

import dev.carlaum.home.dao.HomeDAO;
import dev.carlaum.home.model.Home;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class DelHomeCommand extends CommandBase {

    private final HomeDAO homeDAO;

    public DelHomeCommand(HomeDAO homeDAO) {
        super("delhome");
        this.homeDAO = homeDAO;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (args.length != 1) {
            player.sendMessage("§cUtilize: /delhome <nome>.");
            return;
        }
        List<Home> homesByPlayerId = homeDAO.findHomesByPlayerId(player.getUniqueId());
        String homeName = args[0];

        if (homesByPlayerId.stream().noneMatch(x -> x.getName().equalsIgnoreCase(homeName))) {
            player.sendMessage("§cVocê não tem home com o nome '" + homeName + "'.");
            return;
        }

        Home home = homesByPlayerId.stream().filter(x -> x.getName().equalsIgnoreCase(homeName)).findAny().orElse(null);

        homeDAO.delete(home);
        player.sendMessage("§aVocê deletou a home '" + homeName + "'.");
    }
}
