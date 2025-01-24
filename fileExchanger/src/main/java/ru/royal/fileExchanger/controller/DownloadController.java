package ru.royal.fileExchanger.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.util.UriUtils;
import ru.royal.fileExchanger.entities.Directory;
import ru.royal.fileExchanger.entities.File;
import ru.royal.fileExchanger.repository.LinkRepository;
import ru.royal.fileExchanger.service.DirectoryService;
import ru.royal.fileExchanger.service.DownloadService;
import ru.royal.fileExchanger.service.FileService;
import ru.royal.fileExchanger.service.LinkService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static ru.royal.fileExchanger.Utils.getContentType;

@Controller
public class DownloadController {

    private final FileService fileService;
    private final LinkService linkService;
    private final DownloadService downloadService;
    private final DirectoryService directoryService;
    private final LinkRepository linkRepository;

    @Autowired
    public DownloadController(FileService fileService, LinkService linkService, DownloadService downloadService, DirectoryService directoryService, LinkRepository linkRepository) {
        this.fileService = fileService;
        this.linkService = linkService;
        this.downloadService = downloadService;
        this.directoryService = directoryService;
        this.linkRepository = linkRepository;
    }

    @GetMapping("/download/{hash}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String hash) {
        String fileKey = linkService.getFileKeyByHash(hash);

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

    @GetMapping("/downloadDirectory/{hash}")
    public ResponseEntity<Resource> downloadDirectoryAsZip(@PathVariable String hash) throws IOException {
        Directory directory = linkRepository.findDirectoryByLinkHash(hash).orElseThrow(RuntimeException::new);

        // Создаем путь к временной директории
        String tempDir = System.getProperty("java.io.tmpdir");
        String outputZipPath = tempDir + "/directory_" + directory.getName() + ".zip";

        // Создаем ZIP-архив
        directoryService.downloadDirectoryAsZip(hash, outputZipPath);

        // Читаем ZIP-архив как ресурс
        java.io.File zipFile = new java.io.File(outputZipPath);
        if (!zipFile.exists()) {
            throw new RuntimeException("Файл архива не найден: " + outputZipPath);
        }


        Resource resource = new org.springframework.core.io.FileSystemResource(zipFile);

        // Формируем заголовки для скачивания файла
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + zipFile.getName() + "\"")
                .contentLength(zipFile.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }


}
