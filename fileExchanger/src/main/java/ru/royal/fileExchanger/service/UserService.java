package ru.royal.fileExchanger.service;

import ru.royal.fileExchanger.entities.User;

import java.util.Optional;

public interface UserService {
    void save(User user) throws Exception;

    User findByUsername(String username);

    void deleteUser(String username);

    Optional<User> findById(Long id);

    boolean isUpdatePassword(User user, String password, String newPassword, String confirmPassword);

    boolean verifyEmail(String token);
    void updateEmail(User user, String newEmail) throws Exception;
    void generateVerificationToken(User user);
    User findByEmail(String email);
    void createPasswordResetToken(String email, String token) throws Exception;
    void resetPassword(String token, String newPassword) throws Exception;

}
