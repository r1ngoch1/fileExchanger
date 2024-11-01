package ru.royal.fileExchanger.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import ru.royal.fileExchanger.entities.User;
import java.util.List;

@RepositoryRestResource
public interface UserRepository extends CrudRepository<User,Long> {

    /**
     *
     * @param username
     * @param password
     * @return возвращает пользователя по логину и паролю
     */
    User findByUsernameAndPassword(String username, String password);

    /**
     *
     * @param username
     * @return возвращает пользователя по логину
     */
    User findByUsername(String username);

    /**
     *
     * @param username
     * удаляет пользователя
     */
    void deleteByUsername(String username);



}
