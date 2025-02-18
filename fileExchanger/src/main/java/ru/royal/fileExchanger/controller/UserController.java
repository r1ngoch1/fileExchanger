package ru.royal.fileExchanger.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.royal.fileExchanger.SecurityUtils;
import ru.royal.fileExchanger.entities.User;
import ru.royal.fileExchanger.service.EmailService;
import ru.royal.fileExchanger.service.UserService;

import java.util.UUID;

@Controller
public class UserController {

    private final UserService userService;
    private final SecurityUtils securityUtils;
    private final EmailService emailService;

    @Autowired
    public UserController(UserService userService, SecurityUtils securityUtils, EmailService emailService) {
        this.userService = userService;
        this.securityUtils = securityUtils;
        this.emailService = emailService;
    }

    @GetMapping("/profile")
    public String viewProfile(Model model) {
        User user = securityUtils.getCurrentUser();
        model.addAttribute("user", user); // Передаем данные текущего пользователя в модель
        return "profile"; // Шаблон для отображения профиля
    }

    @GetMapping("/profile/edit")
    public String editProfile(Model model) {
        User user = securityUtils.getCurrentUser();
        model.addAttribute("user", user); // Передаем данные текущего пользователя для редактирования
        return "editProfile"; // Шаблон для редактирования профиля
    }

    @PostMapping("/profile/updatePassword")
    public String updatePassword(Model model, @RequestParam(required = false) String currentPassword,
                                 @RequestParam(required = false) String newPassword,
                                 @RequestParam(required = false) String confirmPassword) {
        User user = securityUtils.getCurrentUser();
        if (userService.isUpdatePassword(user, currentPassword, newPassword, confirmPassword)) {
            return "redirect:/profile";
        } else {
            model.addAttribute("message", "Password does not match");
        }
        return "exceptionMessage";
    }


    @PostMapping("/profile/updateEmail")
    public String updateEmail(@RequestParam("email") String email, Model model) {
        User user = securityUtils.getCurrentUser();

        try {
            userService.updateEmail(user, email);
            model.addAttribute("message", "На указанный email отправлено письмо с подтверждением.");
        } catch (Exception e) {
            System.err.println("Ошибка при обновлении email: " + e.getMessage());
            model.addAttribute("message", "Ошибка при обновлении email.");
            return "editProfile";
        }

        return "editProfile";
    }


    @GetMapping("/verify")
    public String verifyEmail(@RequestParam("token") String token, Model model) {
        if (userService.verifyEmail(token)) {
            model.addAttribute("message", "Email успешно подтвержден!");
            return "redirect:/profile";
        } else {
            model.addAttribute("message", "Неверный или истекший токен!");
            return "exceptionMessage";
        }
    }

    @GetMapping("/forgot-password")
    public String getForgotPassword() {
        return "forgotPassword";
    }

    @PostMapping("/password/reset")
    public String resetPassword(@RequestParam("email") String email, Model model) {
        try {
            String token = UUID.randomUUID().toString();
            userService.createPasswordResetToken(email, token);

            String resetLink = "http://158.160.172.163:8080/password/reset/confirm?token=" + token;
            emailService.sendPasswordResetEmail(email, resetLink);

            model.addAttribute("message", "На указанный email отправлено письмо с инструкцией по восстановлению пароля.");
        } catch (Exception e) {
            e.printStackTrace();

            model.addAttribute("message", "Ошибка при отправке письма для восстановления пароля.");
        }
        return "forgotPassword";
    }

    @GetMapping("/password/reset/confirm")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        model.addAttribute("token", token);
        return "changePasswordAfterForgot";
    }

    @PostMapping("/password/reset/confirm")
    public String resetPassword(@RequestParam("token") String token,
                                @RequestParam("newPassword") String newPassword,
                                @RequestParam("confirmPassword") String confirmPassword,
                                Model model) {
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("message", "Пароли не совпадают.");
            return "changePasswordAfterForgot";
        }
        try {
            userService.resetPassword(token, newPassword);
            model.addAttribute("message", "Пароль успешно изменен.");
            return "login";
        } catch (Exception e) {
            model.addAttribute("message", "Ошибка при сбросе пароля.");
            return "changePasswordAfterForgot";
        }
    }


}
