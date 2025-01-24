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
        Link link = linkService.createLink(fileId, 1,false);
        String generatedLink = "/download/" + link.getLinkHash();
        model.addAttribute("generatedLink", generatedLink);
        return "generatedLinkPage";
    }

    @PostMapping("/generateLinkDirectory/{directoryId}")
    public String generateLinkForDirectory(@PathVariable Long directoryId, Model model) {
        Link link = linkService.createLink(directoryId, 1, true);
        String generatedLink = "/downloadDirectory/" + link.getLinkHash();
        model.addAttribute("generatedLink", generatedLink);
        return "generatedLinkPage";
    }



}
