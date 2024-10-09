package ru.royal.fileExchanger.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.royal.fileExchanger.dao.crudInterface.CrudRepository;
import ru.royal.fileExchanger.entities.User;


import java.util.List;

@Component
public class UserRepository implements CrudRepository<User, Long> {

    private final List<User> userSource;

    @Autowired
    public UserRepository(List<User> userSource) {
        this.userSource = userSource;
    }


    @Override
    public void create(User user) {
        userSource.add(user);
    }

    @Override
    public User read(Long id) {
        for (User u : userSource) {
            if (u.getId().equals(id)) {
                return u;
            }
        }
        return null;
    }


    @Override
    public void update(User user) {
        int i = 0;
        for (User u : userSource) {
            if (u.getId().equals(user.getId())) {
                userSource.set(i, u);
            }
            i++;
        }
    }

    @Override
    public void delete(Long id) {
        User user = read(id);
        userSource.remove(user);
    }


}
