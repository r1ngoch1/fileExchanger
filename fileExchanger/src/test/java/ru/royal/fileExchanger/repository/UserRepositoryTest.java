package ru.royal.fileExchanger.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import ru.royal.fileExchanger.entities.Role;
import ru.royal.fileExchanger.entities.User;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testUser");
        testUser.setPassword("password123");
        testUser.setEmail("test@example.com");
        testUser.setVerificationToken("token123");
        testUser.setUserRole(Collections.singleton(Role.valueOf("ADMIN")));
        entityManager.persist(testUser);

        User user2 = new User();
        user2.setUsername("userWithoutRole");
        user2.setPassword("pass456");
        entityManager.persist(user2);

        entityManager.flush();
    }

    @Test
    void findByUsernameAndPassword_ShouldReturnUser() {
        User found = userRepository.findByUsernameAndPassword("testUser", "password123");
        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void findByUsernameAndPassword_ShouldReturnNullForWrongCredentials() {
        User found = userRepository.findByUsernameAndPassword("wrongUser", "wrongPass");
        assertThat(found).isNull();
    }

    @Test
    void findByUsername_ShouldReturnUser() {
        User found = userRepository.findByUsername("testUser");
        assertThat(found).isNotNull();
        assertThat(found.getVerificationToken()).isEqualTo("token123");
    }

    @Test
    void deleteByUsername_ShouldRemoveUser() {
        userRepository.deleteByUsername("testUser");
        assertThat(userRepository.findByUsername("testUser")).isNull();
    }

    @Test
    void countByUserRole_ShouldReturnCorrectCount() {
        int count = userRepository.countByUserRole(Role.ADMIN);
        assertThat(count).isEqualTo(1);
    }

    @Test
    void countByUserRole_ShouldReturnZeroForNonExistingRole() {

        int count = userRepository.countByUserRole(Role.USER);
        assertThat(count).isEqualTo(0);
    }

    @Test
    void findByVerificationToken_ShouldReturnUser() {
        User found = userRepository.findByVerificationToken("token123");
        assertThat(found).isNotNull();
        assertThat(found.getUsername()).isEqualTo("testUser");
    }

    @Test
    void findByEmail_ShouldReturnUser() {
        User found = userRepository.findByEmail("test@example.com");
        assertThat(found).isNotNull();
        assertThat(found.getUsername()).isEqualTo("testUser");
    }

    @Test
    void findByEmail_ShouldReturnNullForNonExistingEmail() {
        User found = userRepository.findByEmail("nonexisting@example.com");
        assertThat(found).isNull();
    }
}
