package ru.royal.fileExchanger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.royal.fileExchanger.entities.User;
import ru.royal.fileExchanger.repository.FileRepository;
import ru.royal.fileExchanger.repository.UserRepository;
import ru.royal.fileExchanger.service.UserService;

import java.time.LocalDateTime;


@SpringBootTest
class UserTest {
    private final UserRepository userRepository;
    private final FileRepository fileRepository;
    private final UserService userService;

    @Autowired
    UserTest(final UserRepository userRepository, final FileRepository fileRepository, final UserService userService) {
        this.userRepository = userRepository;
        this.fileRepository = fileRepository;
        this.userService = userService;
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
    @Test
    void testUserService(){
        String username = "TaylerDerdan1";
        User user = new User();
        user.setUsername(username);
        user.setPassword("aaa123");
        user.setEmail("aaaarff@mail.ru");
        user.setDateOfRegistration(LocalDateTime.now().toString());
        userService.save(user);
        Assertions.assertNotNull(userService.findByUsername(username));
        Assertions.assertEquals(user.getId(), userService.findByUsername(username).getId());

        userService.deleteUser(username);
    }

    @Test
    void testTransactionalDelete(){
        userService.deleteUser("royal");
    }




}
