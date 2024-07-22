package dev.carlaum.home.event;

import dev.carlaum.home.dao.HomeDAO;
import dev.carlaum.home.dao.UserDAO;
import dev.carlaum.home.model.User;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class UserCache implements Listener {

    private final HomeDAO homeDAO;
    private final UserDAO userDAO;

    public UserCache(HomeDAO homeDAO, UserDAO userDAO) {
        this.homeDAO = homeDAO;
        this.userDAO = userDAO;
    }

    @EventHandler
    public void asyncPlayerPreLoginEvent(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED) {

            this.homeDAO.loadByPlayerId(event.getUniqueId());

            User user = new User(event.getUniqueId(), event.getName());
            this.userDAO.save(user);

        }
    }

    @EventHandler
    public void playerQuitEvent(PlayerQuitEvent event) {
        this.homeDAO.unloadByPlayerId(event.getPlayer().getUniqueId());
    }
}
