package ru.royal.fileExchanger.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.royal.fileExchanger.entities.Role;
import ru.royal.fileExchanger.entities.User;
import ru.royal.fileExchanger.repository.FileRepository;
import ru.royal.fileExchanger.repository.LinkRepository;
import ru.royal.fileExchanger.repository.UserRepository;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final FileRepository fileRepository;
    private final LinkRepository linkRepository;
    private final FileService fileService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, FileRepository fileRepository, LinkRepository linkRepository,
                           FileService fileService, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.fileRepository = fileRepository;
        this.linkRepository = linkRepository;
        this.fileService = fileService;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Override
    public void save(User user) throws Exception {
        User userFromDb = userRepository.findByUsername(user.getUsername());
        if (userFromDb != null) {
            throw new Exception("user exist");
        }
        user.setUserRole(Collections.singleton(Role.USER));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional
    public void deleteUser(String username) {
        fileService.updateActivityFilesByUser(username);
        userRepository.deleteByUsername(username);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User myAppUser = userRepository.findByUsername(username);
        if (myAppUser == null) {
            throw new UsernameNotFoundException("Пользователь не найден: " + username);
        }
        org.springframework.security.core.userdetails.User user = new org.springframework.security.core.userdetails.User(
                myAppUser.getUsername(), myAppUser.getPassword(), mapRoles(myAppUser.getUserRole()));
        return user;

    }

    public Collection<GrantedAuthority> mapRoles(Set<Role> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isUpdatePassword(User user, String password, String newPassword, String confirmPassword) {
        if (passwordEncoder.matches(password, user.getPassword()) && newPassword.equals(confirmPassword)) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public void updateEmail(User user, String newEmail) throws Exception {
        // Проверяем, существует ли уже пользователь с таким email
        User existingUser = userRepository.findByEmail(newEmail);
        if (existingUser != null && !existingUser.getId().equals(user.getId())) {
            throw new Exception("Email already exists");
        }

        // Логика обновления email
        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);
        user.setEmail(newEmail);

        // Сохранение пользователя
        userRepository.save(user);
        System.out.println("Email успешно обновлен для пользователя: " + user.getUsername());

        // Отправка email с подтверждением
        String verificationLink = "http://localhost:8080/verify?token=" + token;
        emailService.sendVerificationEmail(newEmail, verificationLink);
    }

    @Override
    public boolean verifyEmail(String token) {


        User user = userRepository.findByVerificationToken(token);
        if (user != null) {
            user.setEmailVerified(true);
            user.setVerificationToken(null);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public void generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);
        userRepository.save(user);
        // Отправка email с ссылкой подтверждения
        String verificationLink = "http://localhost:8080/verify?token=" + token;
        emailService.sendVerificationEmail(user.getEmail(), verificationLink);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void createPasswordResetToken(String email, String token) throws Exception {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new Exception("Пользователь с таким email не найден.");
        }
        user.setVerificationToken(token);
        user.setTokenCreationDate(new Timestamp(System.currentTimeMillis()));
        userRepository.save(user);
    }

    @Override
    public void resetPassword(String token, String newPassword) throws Exception {
        User user = userRepository.findByVerificationToken(token);
        if (user == null || isTokenExpired(user.getTokenCreationDate())) {
            throw new Exception("Недействительный или истекший токен.");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setVerificationToken(null);
        userRepository.save(user);
    }

    private boolean isTokenExpired(Timestamp tokenCreationDate) {
        long expirationTime = 24 * 60 * 60 * 1000; // 24 часа
        return System.currentTimeMillis() - tokenCreationDate.getTime() > expirationTime;
    }


}
