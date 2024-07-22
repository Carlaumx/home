package dev.carlaum.home.dao;

import dev.carlaum.home.model.User;

import java.util.UUID;

public interface UserDAO {

    void createSchema();

    void save(User user);

    UUID findPlayerIdByName(String name);

}