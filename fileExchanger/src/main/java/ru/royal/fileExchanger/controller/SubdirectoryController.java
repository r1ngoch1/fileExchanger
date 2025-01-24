package ru.royal.fileExchanger.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.royal.fileExchanger.entities.Directory;
import ru.royal.fileExchanger.entities.File;
import ru.royal.fileExchanger.service.DirectoryService;
import ru.royal.fileExchanger.service.FileService;






import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/contents")
public class SubdirectoryController {

    private final DirectoryService directoryService;
    private final FileService fileService;

    @Autowired
    public SubdirectoryController(DirectoryService directoryService, FileService fileService) {
        this.directoryService = directoryService;
        this.fileService = fileService;
    }

    @GetMapping("/{directoryId}")
    public String viewSubdirectory(@PathVariable Long directoryId, Model model) {
        // Получаем текущую директорию
        Directory directory = directoryService.getDirectoryById(directoryId);

        // Получаем содержимое текущей директории
        List<File> files = fileService.getFilesInDirectory(directoryId);
        List<Directory> subdirectories = directoryService.getSubdirectories(directoryId);
        System.out.println("Directories: " + subdirectories);

        model.addAttribute("directory", directory); // Текущая директория
        model.addAttribute("files", files); // Файлы внутри директории
        model.addAttribute("subdirectories", subdirectories); // Дочерние директории

        return "directoryContents"; // HTML-шаблон для вложенных директорий
    }

    @PostMapping("/{directoryId}/create")
    public String createSubdirectory(
            @PathVariable Long directoryId, // ID текущей директории
            @RequestParam String name) {
        directoryService.createDirectory(name, directoryId); // Указываем текущую директорию как родительскую
        return "redirect:/contents/" + directoryId; // Возвращаемся в текущую директорию
    }

    @GetMapping("/{directoryId}/upload")
    public String showUploadFilePage(@PathVariable Long directoryId, Model model) {
        model.addAttribute("directoryId", directoryId);
        return "uploadSubDir"; // Имя шаблона
    }

    @PostMapping("/{directoryId}/upload")
    public String uploadFile(@PathVariable Long directoryId,
                             @RequestParam("file") MultipartFile file) throws IOException {
        fileService.uploadFile(file, directoryId);
        return "redirect:/contents/" + directoryId; // Возвращаемся к содержимому директории
    }

    @DeleteMapping("/deleteDirectory/{directoryId}")
    public String deleteDirectory(@PathVariable("directoryId") Long directoryId) {
        try {
            directoryService.deleteDirectory(directoryId);
        } catch (Exception e) {
            // Обработка ошибок
            System.err.println("Ошибка при удалении директории: " + e.getMessage());
        }
        return "redirect:/home";  // Перенаправляем на главную страницу
    }







}


