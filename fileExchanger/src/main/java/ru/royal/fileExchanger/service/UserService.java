package ru.royal.fileExchanger.service;

import ru.royal.fileExchanger.entities.User;

public interface UserService {
    void save(User user) throws Exception;

    User findByUsername(String username);

    void deleteUser(String username);
}
