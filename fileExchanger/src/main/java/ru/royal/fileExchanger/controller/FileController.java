package ru.royal.fileExchanger.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.royal.fileExchanger.dao.FileRepositoryCustom;
import ru.royal.fileExchanger.entities.File;


import java.util.List;

@RestController
@RequestMapping("/custom/files")
public class FileController {

    @Autowired
    private FileRepositoryCustom fileRepositoryCustom;

    @GetMapping("/findByFileName")
    public List<File> findByFileName(@RequestParam String fileName){
        return fileRepositoryCustom.findByFileName(fileName);
    }

    @GetMapping("/findByUsername")
    public List<File> findByUsername(@RequestParam String username){
        return fileRepositoryCustom.findByUsername(username);
    }
}
