package ru.royal.fileExchanger.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.royal.fileExchanger.entities.Role;
import ru.royal.fileExchanger.entities.User;
import ru.royal.fileExchanger.repository.UserRepository;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FileService fileService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void save_ShouldSaveNewUser() throws Exception {
        User newUser = new User();
        newUser.setUsername("newUser");
        newUser.setPassword("password");

        when(userRepository.findByUsername("newUser")).thenReturn(null);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        userService.save(newUser);

        verify(userRepository).save(argThat(user ->
                user.getUsername().equals("newUser") &&
                        user.getPassword().equals("encodedPassword") &&
                        user.getUserRole().contains(Role.USER)
        ));
    }

    @Test
    void save_ShouldThrowWhenUserExists() {
        User existingUser = new User();
        when(userRepository.findByUsername("existingUser")).thenReturn(existingUser);

        assertThrows(Exception.class, () -> userService.save(existingUser));
    }

    @Test
    void deleteUser_ShouldUpdateFilesAndDeleteUser() {
        String username = "testUser";

        userService.deleteUser(username);

        verify(fileService).updateActivityFilesByUser(username);
        verify(userRepository).deleteByUsername(username);
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetails() {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("encodedPass");
        user.setUserRole(Collections.singleton(Role.ADMIN));

        when(userRepository.findByUsername("testUser")).thenReturn(user);

        UserDetails userDetails = userService.loadUserByUsername("testUser");

        assertEquals("testUser", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void loadUserByUsername_ShouldThrowWhenUserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () ->
                userService.loadUserByUsername("unknown")
        );
    }

    @Test
    void isUpdatePassword_ShouldUpdateWhenValid() {
        User user = new User();
        user.setPassword("oldEncoded");

        when(passwordEncoder.matches("oldPass", "oldEncoded")).thenReturn(true);
        when(passwordEncoder.encode("newPass")).thenReturn("newEncoded");

        boolean result = userService.isUpdatePassword(
                user, "oldPass", "newPass", "newPass"
        );

        assertTrue(result);
        assertEquals("newEncoded", user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void updateEmail_ShouldGenerateTokenAndSendEmail() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setEmail("old@mail.com");

        when(userRepository.findByEmail("new@mail.com")).thenReturn(null);

        userService.updateEmail(user, "new@mail.com");

        assertNotNull(user.getVerificationToken());
        assertEquals("new@mail.com", user.getEmail());
        verify(emailService).sendVerificationEmail(any(), any());
        verify(userRepository).save(user);
    }

    @Test
    void verifyEmail_ShouldActivateUser() {

        User user = new User();
        user.setVerificationToken("validToken");

        when(userRepository.findByVerificationToken("validToken")).thenReturn(user);


        boolean result = userService.verifyEmail("validToken");

        assertTrue(result);
        assertNull(user.getVerificationToken());
        assertTrue(user.isEmailVerified());
        verify(userRepository).save(user);
    }

    @Test
    void resetPassword_ShouldUpdatePassword() throws Exception {
        User user = new User();
        user.setVerificationToken("validToken");
        user.setTokenCreationDate(new Timestamp(System.currentTimeMillis()));

        when(userRepository.findByVerificationToken("validToken")).thenReturn(user);
        when(passwordEncoder.encode("newPass")).thenReturn("newEncoded");

        userService.resetPassword("validToken", "newPass");

        assertEquals("newEncoded", user.getPassword());
        assertNull(user.getVerificationToken());
        verify(userRepository).save(user);
    }
}
