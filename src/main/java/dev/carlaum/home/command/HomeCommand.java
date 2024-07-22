package dev.carlaum.home.command;

import dev.carlaum.home.HomePlugin;
import dev.carlaum.home.dao.HomeDAO;
import dev.carlaum.home.dao.UserDAO;
import dev.carlaum.home.enums.Type;
import dev.carlaum.home.manager.CooldownManager;
import dev.carlaum.home.model.Home;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;

public class HomeCommand extends CommandBase {

    private final HomeDAO homeDAO;
    private final UserDAO userDAO;

    public HomeCommand(HomeDAO homeDAO, UserDAO userDAO) {
        super("home");
        this.homeDAO = homeDAO;
        this.userDAO = userDAO;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (args.length != 1) {
            sender.sendMessage("§cUtilize: /home <nome>.");
            return;
        }
        if (!player.hasPermission("home.ignorecooldown") && CooldownManager.hasCooldown(player)) {
            player.sendMessage("§cAguarde " + CooldownManager.getRemainingTimeToString(player) + " para teletransportar novamente.");
            return;
        }

        if (args[0].contains(":")) {
            String[] split = args[0].split(":");
            String playerName = split[0];
            String homeName = split[1];

            if (userDAO.findPlayerIdByName(playerName) == null) {
                player.sendMessage("§cEste player não existe.");
                return;
            }

            UUID playerIdByName = userDAO.findPlayerIdByName(playerName);

            homeDAO.findHomesByPlayerId(playerIdByName).stream().filter(x -> x.getName().equalsIgnoreCase(homeName)).findAny().ifPresentOrElse(home -> {
                if (!playerName.equalsIgnoreCase(player.getName()) && home.getType() == Type.PRIVATE) {
                    player.sendMessage("§cA home '" + home.getName() + "' de " + playerName + " é do tipo " + Type.PRIVATE.getName() + ".");
                    return;
                }
                teleportToHome(player, home);
                sender.sendMessage("§aTeletransportado para a home '" + home.getName() + "' de " + playerName + ".");
            }, () -> player.sendMessage("§cO player " + playerName + " não tem a home '" + homeName + "'."));


            return;
        }

        List<Home> homesById = homeDAO.findHomesByPlayerId(player.getUniqueId());

        homesById.stream()
                .filter(x -> x.getName().equalsIgnoreCase(args[0]))
                .findAny()
                .ifPresentOrElse(home -> {
                    teleportToHome(player, home);
                    sender.sendMessage("§aTeletransportado para a home '" + home.getName() + "'.");
                }, () -> player.sendMessage("§cA home '" + args[0] + "' não existe."));


    }

    private void teleportToHome(Player player, Home home) {
        Bukkit.getScheduler().runTask(HomePlugin.INSTANCE, () -> {
            player.teleport(home.getLocation());
            if (Home.SOUND != null) player.playSound(player.getLocation(), Home.SOUND, 1F, 1F);
            if (Home.PARTICLE != null) startParticleAnimation(player);
        });

        CooldownManager.updateCooldown(player, Home.COOLDOWN);
    }

    private void startParticleAnimation(Player player) {
        Location loc = player.getLocation();
        new BukkitRunnable() {
            double radius = 0;
            final double maxRadius = 1.4;
            final double radiusIncrement = 0.4;
            final int particlesPerTick = 20;

            @Override
            public void run() {
                if (radius > maxRadius) {
                    this.cancel();
                    return;
                }

                for (int i = 0; i < particlesPerTick; i++) {
                    double angle = 2 * Math.PI * i / particlesPerTick;
                    double x = radius * Math.cos(angle);
                    double z = radius * Math.sin(angle);
                    Location particleLoc = loc.clone().add(x, 0, z);
                    player.getWorld().spawnParticle(Home.PARTICLE, particleLoc, 0);
                }

                radius += radiusIncrement;
            }
        }.runTaskTimer(HomePlugin.INSTANCE, 0, 2);
    }

}
