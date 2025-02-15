package ru.royal.fileExchanger.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.royal.fileExchanger.SecurityUtils;
import ru.royal.fileExchanger.entities.Directory;
import ru.royal.fileExchanger.entities.File;
import ru.royal.fileExchanger.entities.User;
import ru.royal.fileExchanger.service.DirectoryService;
import ru.royal.fileExchanger.service.FileService;
import ru.royal.fileExchanger.service.UserService;
import org.springframework.ui.Model;


import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "*")
@Controller
public class FileController {


    private FileService fileService;
    private UserService userService;
    private DirectoryService directoryService;
    private SecurityUtils securityUtils;

    @Autowired
    public FileController(FileService fileService, UserService userService, DirectoryService directoryService, SecurityUtils securityUtils) {
        this.fileService = fileService;
        this.userService = userService;
        this.directoryService = directoryService;
        this.securityUtils = securityUtils;
    }


    @GetMapping("/home/upload")
    public String getUpload() {
        return "upload";
    }

    @PostMapping("/home/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file,
                             @RequestParam(value = "directoryId", required = false) Long directoryId) throws IOException {
        Directory parentDirectory = null;

        if (directoryId != null) {
            parentDirectory = directoryService.getDirectoryById(directoryId);

            if (parentDirectory == null) {
                throw new IllegalArgumentException("Директория с ID " + directoryId + " не найдена.");
            }
        }
        fileService.uploadFile(file, parentDirectory != null ? parentDirectory.getId() : null);

        return "redirect:/home";
    }

    @DeleteMapping("/deleteFile/{fileId}")
    public String deleteFile(@PathVariable("fileId") Long fileId) {
        fileService.deleteFile(fileId);
        return "redirect:/home";
    }


}
