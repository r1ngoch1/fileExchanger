package ru.royal.fileExchanger.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.royal.fileExchanger.entities.User;

import java.util.List;

public interface UserRepository extends CrudRepository<User,Long> {

    List<User> findByUsernameAndPassword(String username, String password);



}
