package dev.carlaum.home.dao;

import dev.carlaum.home.enums.Type;
import dev.carlaum.home.model.Home;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class HomeDAOImpl implements HomeDAO {

    private final HashMap<UUID, List<Home>> userCache = new HashMap<>();

    private Connection connection;

    @Override
    public void createSchema() {
        try(PreparedStatement home_schema = connection.prepareStatement("""
                    CREATE TABLE IF NOT EXISTS home (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        loc_world VARCHAR(100) NOT NULL,
                        loc_x DOUBLE NOT NULL,
                        loc_y DOUBLE NOT NULL,
                        loc_z DOUBLE NOT NULL,
                        loc_yaw FLOAT NOT NULL,
                        loc_pitch FLOAT NOT NULL,
                        type INT NOT NULL,
                        user_id UUID NOT NULL,
                        FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
                        UNIQUE KEY idx_user_name (user_id, name)
                    );
                    """)) {
            home_schema.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void loadByPlayerId(UUID id) {
        userCache.put(id, findHomesByPlayerId(id));
    }

    @Override
    public void unloadByPlayerId(UUID id) {
        userCache.remove(id);
    }

    @Override
    public List<Home> findHomesByPlayerId(UUID id) {
        if (userCache.containsKey(id)) return userCache.get(id);

        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM home WHERE user_id = ?")) {
            List<Home> homeList = new ArrayList<>();

            ps.setString(1, id.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Home home = new Home();
                    home.setName(rs.getString("name"));
                    home.setLocation(new Location(
                            Bukkit.getWorld(rs.getString("loc_world")),
                            rs.getDouble("loc_x"),
                            rs.getDouble("loc_y"),
                            rs.getDouble("loc_z"),
                            rs.getFloat("loc_yaw"),
                            rs.getFloat("loc_pitch")
                    ));
                    home.setType(Type.fromId(rs.getInt("type")));
                    home.setUserId(id);

                    homeList.add(home);
                }
            }

            return homeList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void save(Home home) {
        if (userCache.containsKey(home.getUserId()))
            userCache.get(home.getUserId())
                    .stream()
                    .filter(x -> x.getName().equalsIgnoreCase(home.getName()))
                    .findAny()
                    .ifPresentOrElse(y -> y.setType(home.getType()), () -> userCache.get(home.getUserId()).add(home));

        try (PreparedStatement ps = connection.prepareStatement("INSERT INTO home(name, loc_world, loc_x, loc_y, loc_z, loc_yaw, loc_pitch, type, user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE type = VALUES(type);")) {
            ps.setString(1, home.getName());

            Location location = home.getLocation();
            ps.setString(2, location.getWorld().getName());
            ps.setDouble(3, location.getX());
            ps.setDouble(4, location.getY());
            ps.setDouble(5, location.getZ());
            ps.setFloat(6, location.getYaw());
            ps.setFloat(7, location.getPitch());
            ps.setInt(8, home.getType().getId());
            ps.setString(9, home.getUserId().toString());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Home home) {
        if (userCache.containsKey(home.getUserId())) userCache.get(home.getUserId()).remove(home);

        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM home WHERE name = ? AND user_id = ?;")) {

            ps.setString(1, home.getName());
            ps.setString(2, home.getUserId().toString());

            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
