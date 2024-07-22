package dev.carlaum.home.command;

import dev.carlaum.home.dao.HomeDAO;
import dev.carlaum.home.enums.Type;
import dev.carlaum.home.model.Home;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SetHomeCommand extends CommandBase {

    private final HomeDAO homeDAO;

    public SetHomeCommand(HomeDAO homeDAO) {
        super("sethome");
        this.homeDAO = homeDAO;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (args.length != 1) {
            sender.sendMessage("§cUtilize: /sethome <nome>.");
            return;
        }
        List<Home> homesById = homeDAO.findHomesByPlayerId(player.getUniqueId());
        String homeName = args[0];
        if (homesById.stream().anyMatch(x -> x.getName().equalsIgnoreCase(homeName))) {
            player.sendMessage("§cVocê já tem uma home com o nome '" + homeName + "'.");
            return;
        }
        if(homesById.size()>=Home.MAX_HOMES_PER_PLAYER) {
            player.sendMessage("§cVocê já atingiu o máximo de homes.");
            return;
        }
        Home home = new Home();
        home.setName(homeName);
        home.setLocation(player.getLocation());
        home.setUserId(player.getUniqueId());
        home.setType(Type.PRIVATE);

        homeDAO.save(home);

        player.sendMessage("§aHome '" + homeName + "' setada com sucesso!");
    }
}
