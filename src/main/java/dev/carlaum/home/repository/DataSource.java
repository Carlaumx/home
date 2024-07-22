package dev.carlaum.home.repository;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.carlaum.home.HomePlugin;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.SQLException;

public class DataSource {

    private final HikariDataSource ds;

    public DataSource(FileConfiguration config) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:mysql://" + config.getString("mysql.hostname") + ":" + config.getInt("mysql.port") + "/" + config.getString("mysql.database"));
        hikariConfig.setUsername(config.getString("mysql.username"));
        hikariConfig.setPassword(config.getString("mysql.password"));
        hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        try {
            ds = new HikariDataSource(hikariConfig);

            try (Connection connection = ds.getConnection()) {
                if (connection == null || connection.isClosed()) {
                    throw new SQLException("Falha ao estabelecer conexão com o banco de dados.");
                }
            }
        } catch (SQLException e) {
            HomePlugin.INSTANCE.getLogger().severe("Não foi possível estabelecer uma conexão com o banco de dados: " + e.getMessage());
            HomePlugin.INSTANCE.getServer().getPluginManager().disablePlugin(HomePlugin.INSTANCE);
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public void close() {
        ds.close();
    }
}
