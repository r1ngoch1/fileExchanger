package ru.royal.fileExchanger.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.royal.fileExchanger.entities.User;
import ru.royal.fileExchanger.repository.UserRepository;

@Controller
@RequestMapping("/custom/users/view")
public class UserControllerView {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/list")
    public String userListView(Model model) {
        Iterable<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "userlist";
    }
}
