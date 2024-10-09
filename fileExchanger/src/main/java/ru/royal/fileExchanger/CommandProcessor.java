package ru.royal.fileExchanger;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.royal.fileExchanger.service.serviceInterface.UserService;

@Component
public class CommandProcessor
{
    private final UserService userService;

    @Autowired
    public CommandProcessor(UserService userService)
    {
        this.userService = userService;
    }

    public void processCommand(String input)
    {
        String[] commands = input.split(" ");
        switch (commands[0])
        {
            case "Create" ->
            {
                userService.createUser(Long.valueOf(commands[1]),commands[2], commands[3], commands[4] );
                System.out.println("User created");
            }
            case "findById" ->
            {
                System.out.println(userService.findById(Long.valueOf(commands[1])));
            }

            default -> System.out.println("Введена неизвестная команда");
        }

    }
}