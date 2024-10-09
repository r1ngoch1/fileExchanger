package ru.royal.fileExchanger.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import ru.royal.fileExchanger.dao.UserRepository;
import ru.royal.fileExchanger.entities.User;

@Service
public class UserImpl implements UserService{


    private final UserRepository userRepository;

    @Autowired
    public UserImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void createUser(Long id, String email, String login, String password){
        User user = new User();

        user.setId(id);
        user.setEmail(email);
        user.setLogin(login);
        user.setPassword(password);
        user.setDateOfRegistration(LocalDateTime.now().toString());
        user.setActive(true);

        userRepository.create(user);
    }

    @Override
    public User findById(Long id){
        return userRepository.read(id);
    }


    @Override
    public void deleteById(Long id){
        userRepository.delete(id);
    }

    @Override
    public void updateLogin(Long id, String newLogin,String email, String password){
        User user = new User();

        user.setId(id);
        user.setEmail(email);
        user.setLogin(newLogin);
        user.setPassword(password);
        user.setDateOfRegistration(LocalDateTime.now().toString());
        user.setActive(true);

        userRepository.update(user);
    }

    @Override
    public void updatePassword(Long id, String login, String password, String newPassword,String email){
        User user = new User();

        user.setId(id);
        user.setEmail(email);
        user.setLogin(login);
        user.setPassword(newPassword);
        user.setDateOfRegistration(LocalDateTime.now().toString());
        user.setActive(true);

        userRepository.update(user);
    }
}
