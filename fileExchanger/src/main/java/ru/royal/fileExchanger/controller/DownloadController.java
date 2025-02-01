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

import static ru.royal.fileExchanger.Utils.*;

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
    public ResponseEntity<Resource> downloadFile(@PathVariable String hash) throws IOException {
        String fileKey = linkService.getFileKeyByHash(hash);

        Resource fileResource = fileService.downloadFile(fileKey);
        File file = linkService.getFileByLinkHash(hash);
        downloadService.saveDownload(linkService.getLinkByHash(hash));

        String fileName = file.getFileName();
        String encodedFileName = UriUtils.encodePath(fileName, StandardCharsets.UTF_8);

        return downloadFileUtil(file, fileResource,encodedFileName);

    }

    @GetMapping("/downloadDirectory/{hash}")
    public ResponseEntity<Resource> downloadDirectoryAsZip(@PathVariable String hash) throws IOException {
        Directory directory = linkRepository.findDirectoryByLinkHash(hash).orElseThrow(RuntimeException::new);

        String tempDir = System.getProperty("java.io.tmpdir");
        String outputZipPath = tempDir + "/directory_" + directory.getName() + ".zip";

        directoryService.downloadDirectoryAsZip(hash, outputZipPath);
        downloadService.saveDownload(linkService.getLinkByHash(hash));

        java.io.File zipFile = new java.io.File(outputZipPath);
        if (!zipFile.exists()) {
            throw new RuntimeException("Файл архива не найден: " + outputZipPath);
        }

        Resource resource = new org.springframework.core.io.FileSystemResource(zipFile);

        return downloadDirectory(zipFile,resource);
    }
    



}
