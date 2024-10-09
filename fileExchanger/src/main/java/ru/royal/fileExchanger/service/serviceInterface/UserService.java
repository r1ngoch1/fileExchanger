package ru.royal.fileExchanger.service.serviceInterface;

import ru.royal.fileExchanger.entities.User;


public interface UserService {
    void createUser(Long id, String email, String login, String password);

    User findById(Long id);

    void deleteById(Long id);

    void updateLogin(Long id, String newLogin, String email, String password);

    void updatePassword(Long id, String login, String password, String newPassword,String email);

}
