package ru.royal.fileExchanger.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.royal.fileExchanger.entities.User;

import java.util.List;

public interface UserRepository extends CrudRepository<User,Long> {

    /**
     *
     * @param username
     * @param password
     * @return возвращает пользователя по логину и паролю
     */
    User findByUsernameAndPassword(String username, String password);



}
