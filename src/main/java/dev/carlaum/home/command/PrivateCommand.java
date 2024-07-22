package dev.carlaum.home.command;

import dev.carlaum.home.dao.HomeDAO;
import dev.carlaum.home.enums.Type;
import dev.carlaum.home.model.Home;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class PrivateCommand extends CommandBase {

    private final HomeDAO homeDAO;

    public PrivateCommand(HomeDAO homeDAO) {
        super("privada");
        this.homeDAO = homeDAO;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (args.length != 1) {
            player.sendMessage("§cUtilize: /privada <nome>.");
            return;
        }
        List<Home> homesByPlayerId = homeDAO.findHomesByPlayerId(player.getUniqueId());
        if (homesByPlayerId.stream().noneMatch(x -> x.getName().equalsIgnoreCase(args[0]))) {
            player.sendMessage("§cVocê não tem home com o nome '" + args[0] + "'.");
            return;
        }

        Home home = homesByPlayerId.stream().filter(x -> x.getName().equalsIgnoreCase(args[0])).findAny().orElse(null);
        if(home.getType() == Type.PRIVATE) {
            player.sendMessage("§cA home '" +  home.getName()  + "' já está " + home.getType().getName() + ".");
            return;
        }

        home.setType(Type.PRIVATE);
        homeDAO.save(home);

        player.sendMessage("§aVocê alterou a home '"+ home.getName() + "' para " + home.getType().getName() + ".");

    }
}
