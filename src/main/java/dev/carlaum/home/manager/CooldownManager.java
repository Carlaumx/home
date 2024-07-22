package dev.carlaum.home.manager;

import dev.carlaum.home.HomePlugin;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.text.DecimalFormat;

public class CooldownManager {

    public static boolean hasCooldown(Player player) {
        return player.hasMetadata("cooldown-home") && player.getMetadata("cooldown-home").getFirst().asLong() > System.currentTimeMillis();
    }

    public static void updateCooldown(Player player, int seconds) {
        player.setMetadata("cooldown-home", new FixedMetadataValue(HomePlugin.INSTANCE, System.currentTimeMillis() + (seconds * 1000L)));
    }

    public static long getRemainingTime(Player player) {
        return hasCooldown(player) ? player.getMetadata("cooldown-home").getFirst().asLong() - System.currentTimeMillis() : 0L;
    }

    public static String getRemainingTimeToString(Player player) {
        float d = (float) getRemainingTime(player) / 1000;
        return new DecimalFormat("#.#").format(d) + "s";
    }
}
