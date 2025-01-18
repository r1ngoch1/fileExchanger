package ru.royal.fileExchanger.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import ru.royal.fileExchanger.entities.Directory;
import ru.royal.fileExchanger.entities.File;
import ru.royal.fileExchanger.service.DirectoryService;

import org.springframework.web.bind.annotation.*;
import ru.royal.fileExchanger.service.FileService;

import java.util.List;

@Controller
@RequestMapping("/home")
public class RootDirectoryController {

    private final DirectoryService directoryService;
    private final FileService fileService;

    @Autowired
    public RootDirectoryController(DirectoryService directoryService, FileService fileService) {
        this.directoryService = directoryService;
        this.fileService = fileService;
    }

    @GetMapping
    public String viewRootDirectory(Model model) {
        List<Directory> directories = directoryService.getRootDirectoriesByUser();
        List<File> files = fileService.getFilesInRootDirectory();



        model.addAttribute("directories", directories);
        model.addAttribute("files", files);

        return "home"; // HTML-шаблон для корневой директории
    }

    @PostMapping("/create")
    public String createRootDirectory(@RequestParam String name) {
        directoryService.createDirectory(name, 0L); // null указывает, что это корневая директория
        return "redirect:/home";
    }
    @GetMapping("/create")
    public String createRootDirectory() {
        return "createDirectory";
    }

}

