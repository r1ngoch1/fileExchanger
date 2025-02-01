package ru.royal.fileExchanger.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.royal.fileExchanger.SecurityUtils;
import ru.royal.fileExchanger.entities.Download;
import ru.royal.fileExchanger.entities.Link;
import ru.royal.fileExchanger.entities.User;
import ru.royal.fileExchanger.repository.DownloadRepository;
import ru.royal.fileExchanger.repository.LinkRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
public class DownloadServiceImpl implements DownloadService{

    private final DownloadRepository downloadRepository;
    private final LinkRepository linkRepository;
    private final SecurityUtils securityUtils;

    @Autowired
    public DownloadServiceImpl(DownloadRepository downloadRepository, LinkRepository linkRepository, SecurityUtils securityUtils) {
        this.downloadRepository = downloadRepository;
        this.linkRepository = linkRepository;
        this.securityUtils = securityUtils;
    }

    @Override
    public void saveDownload(Link link) {
        Download download = new Download();
        User user = securityUtils.getCurrentUser();

        download.setDownloadAt(Timestamp.valueOf(LocalDateTime.now()));
        download.setDownloadIp("ip");
        download.setUser(user);
        download.setLink(link);

        downloadRepository.save(download);
    }
}
