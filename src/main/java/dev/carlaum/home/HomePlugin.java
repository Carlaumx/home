package dev.carlaum.home;

import dev.carlaum.home.command.*;
import dev.carlaum.home.dao.HomeDAO;
import dev.carlaum.home.dao.HomeDAOImpl;
import dev.carlaum.home.dao.UserDAO;
import dev.carlaum.home.dao.UserDAOImpl;
import dev.carlaum.home.event.UserCache;
import dev.carlaum.home.repository.DataSource;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.Arrays;

@Getter
public final class HomePlugin extends JavaPlugin {

    public static HomePlugin INSTANCE;

    private DataSource dataSource;

    private HomeDAO homeDAO;
    private UserDAO userDAO;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        INSTANCE = this;

        this.dataSource = new DataSource(this.getConfig());

        try {
            this.userDAO = new UserDAOImpl(dataSource.getConnection());
            this.userDAO.createSchema();

            this.homeDAO = new HomeDAOImpl(dataSource.getConnection());
            this.homeDAO.createSchema();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Bukkit.getPluginManager().registerEvents(new UserCache(homeDAO, userDAO), this);

        registerCommands(
                new HomeCommand(homeDAO, userDAO),
                new HomesCommand(homeDAO),
                new SetHomeCommand(homeDAO),
                new DelHomeCommand(homeDAO),
                new PrivateCommand(homeDAO),
                new PublicCommand(homeDAO)
        );

        Bukkit.getOnlinePlayers().forEach(x -> this.homeDAO.loadByPlayerId(x.getUniqueId()));
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(x -> this.homeDAO.unloadByPlayerId(x.getUniqueId()));

        this.dataSource.close();
    }

    public void registerCommands(Command... commands) {
        Arrays.stream(commands).forEach(x -> this.getServer().getCommandMap().register(x.getName(), x));
    }
}
