package ru.royal.fileExchanger.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;
import ru.royal.fileExchanger.Utils;
import ru.royal.fileExchanger.entities.File;
import ru.royal.fileExchanger.entities.Link;
import ru.royal.fileExchanger.service.DownloadService;
import ru.royal.fileExchanger.service.FileService;
import ru.royal.fileExchanger.service.LinkService;

import java.nio.charset.StandardCharsets;

import static ru.royal.fileExchanger.Utils.getContentType;

@Controller
public class LinkController {
    private final LinkService linkService;
    private final FileService fileService;
    private final DownloadService downloadService;

    public LinkController(LinkService linkService, FileService fileService, DownloadService downloadService) {
        this.linkService = linkService;
        this.fileService = fileService;
        this.downloadService = downloadService;
    }

    @PostMapping("/generateLink/{fileId}")
    public String generateLink(@PathVariable Long fileId, Model model) {
        Link link = linkService.createLink(fileId, 1);
        String generatedLink = "/download/" + link.getLinkHash(); // Формируем ссылку

        model.addAttribute("generatedLink", generatedLink); // Добавляем в модель

        // Возвращаем название шаблона
        return "generatedLinkPage";
    }


    @GetMapping("/download/{hash}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String hash) {
        String fileKey = linkService.getFileKeyByHash(hash); // Пример метода в LinkService

        // Загружаем файл через S3Service
        Resource fileResource = fileService.downloadFile(fileKey);
        File file = linkService.getFileByLinkHash(hash);
        downloadService.saveDownload(linkService.getLinkByHash(hash));

        String fileName = file.getFileName();
        String encodedFileName = UriUtils.encodePath(fileName, StandardCharsets.UTF_8);

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"");
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");

        // Возвращаем файл пользователю
        return ResponseEntity.ok()
                .headers(header)
                .contentLength(file.getFileSize())
                .contentType(MediaType.parseMediaType(getContentType(file.getFileType())))
                .body(fileResource);

    }

}
