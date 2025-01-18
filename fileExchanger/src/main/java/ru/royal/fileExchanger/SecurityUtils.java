package ru.royal.fileExchanger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.royal.fileExchanger.entities.User;
import ru.royal.fileExchanger.service.UserService;

@Component
public class SecurityUtils {
    private final UserService userService;


    @Autowired
    public SecurityUtils(@Lazy UserService userService) {
        this.userService = userService;
    }
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();  // Получаем имя пользователя из контекста безопасности
            return userService.findByUsername(username);  // Получаем пользователя из базы данных
        }

        throw new RuntimeException("Пользователь не найден");
    }
}
