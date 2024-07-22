package dev.carlaum.home.dao;

import dev.carlaum.home.model.Home;

import java.util.List;
import java.util.UUID;

public interface HomeDAO {

    void createSchema();

    void save(Home home);
    void delete(Home home);

    void loadByPlayerId(UUID uuid);
    void unloadByPlayerId(UUID uuid);

    List<Home> findHomesByPlayerId(UUID uuid);

}
