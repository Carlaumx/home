package dev.carlaum.home.model;

import dev.carlaum.home.HomePlugin;
import dev.carlaum.home.enums.Type;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Home {

    public static final int COOLDOWN;
    public static final Particle PARTICLE;
    public static final Sound SOUND;
    public static final int MAX_HOMES_PER_PLAYER = 10;

    private String name;
    private Location location;
    private Type type;
    private UUID userId;

    static {
        FileConfiguration config = HomePlugin.INSTANCE.getConfig();

        COOLDOWN = config.getInt("home.cooldown");

        String particleName = config.getString("home.particle");
        PARTICLE = particleName.equalsIgnoreCase("desativar") ? null : isValidEnumValue(Particle.class, particleName) ? Particle.valueOf(particleName) : Particle.GLOW;

        String soundName = config.getString("home.sound");
        SOUND = soundName.equalsIgnoreCase("desativar") ? null : isValidEnumValue(Sound.class, soundName) ? Sound.valueOf(soundName) : Sound.ENTITY_ENDERMAN_TELEPORT;

    }

    private static <E extends Enum<E>> boolean isValidEnumValue(Class<E> enumClass, String value) {
        try {
            Enum.valueOf(enumClass, value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
