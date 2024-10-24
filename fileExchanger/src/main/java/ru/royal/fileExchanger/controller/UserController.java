package ru.royal.fileExchanger.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.royal.fileExchanger.repository.UserRepository;

@RestController
@RequestMapping("/custom/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;


}
