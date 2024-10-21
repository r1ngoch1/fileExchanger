package ru.royal.fileExchanger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.royal.fileExchanger.entities.User;
import ru.royal.fileExchanger.repository.FileRepository;
import ru.royal.fileExchanger.repository.UserRepository;

import java.time.LocalDateTime;


@SpringBootTest
class UserTest {
    private final UserRepository userRepository;
    private final FileRepository fileRepository;
    @Autowired
    UserTest(final UserRepository userRepository, final FileRepository fileRepository) {
        this.userRepository = userRepository;
        this.fileRepository = fileRepository;
    }
    @Test
    void testFindByUsernameAndPassword(){
        String username = "ivan";

        User user = new User();
        user.setUsername(username);
        user.setPassword("14235123");
        user.setEmail("khjkgh");
        user.setDateOfRegistration(LocalDateTime.now().toString());
        userRepository.save(user);
        User foundUser = userRepository.findByUsernameAndPassword("ivan","14235123");

        Assertions.assertNotNull(foundUser);
        Assertions.assertEquals(user.getId(), foundUser.getId());
        Assertions.assertEquals(username,foundUser.getUsername());
    }
    @Test
    void testCreateUser(){
        String username = "royal";

        User user = new User();
        user.setUsername(username);
        user.setPassword("14235123");
        user.setEmail("khjkgh");
        user.setDateOfRegistration(LocalDateTime.now().toString());
        userRepository.save(user);

        User foundUser = userRepository.findByUsernameAndPassword("royal","14235123");

        Assertions.assertNotNull(foundUser);
        Assertions.assertEquals(user.getId(), foundUser.getId());
        Assertions.assertEquals(username,foundUser.getUsername());


    }

}
