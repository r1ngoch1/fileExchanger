package ru.royal.fileExchanger.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.royal.fileExchanger.entities.File;
import ru.royal.fileExchanger.entities.User;
import ru.royal.fileExchanger.service.FileService;
import ru.royal.fileExchanger.service.UserService;

import java.util.List;

@Controller
public class MainController {

    private final UserService userService;
    private final FileService fileService;

    @Autowired
    public MainController(UserService userService, FileService fileService) {
        this.userService = userService;
        this.fileService = fileService;
    }


    @GetMapping("/registration")
    public String getRegistration(){
        return "registration";
    }

    @PostMapping("/registration")
    public String adduser(User user, Model model)
    {
        try
        {
            userService.save(user);
            return "redirect:/login";
        }
        catch (Exception ex)
        {
            model.addAttribute("message", "User exists");
            return "registration";
        }
    }
    @GetMapping("/login")
    public String login(){
        return "login";
    }










}
