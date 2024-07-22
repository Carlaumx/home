package dev.carlaum.home.dao;

import dev.carlaum.home.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserDAOImpl implements UserDAO {

    private final Connection connection;

    public UserDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void createSchema() {
        try (PreparedStatement user_schema = connection.prepareStatement(
                """
                        CREATE TABLE IF NOT EXISTS user (
                        id UUID PRIMARY KEY,
                        lastname VARCHAR(30) NOT NULL
                        );
                        """)){
            user_schema.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(User user) {
        try (PreparedStatement ps = connection.prepareStatement("INSERT INTO user(id, lastname) VALUES (?, ?) ON DUPLICATE KEY UPDATE lastname = VALUES(lastname)")) {
            ps.setString(1, user.getId().toString());
            ps.setString(2, user.getLastName());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UUID findPlayerIdByName(String playerName) {
        try (PreparedStatement ps = connection.prepareStatement("SELECT id FROM user WHERE lastname = ?")) {
            ps.setString(1, playerName);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return UUID.fromString(rs.getString("id"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
