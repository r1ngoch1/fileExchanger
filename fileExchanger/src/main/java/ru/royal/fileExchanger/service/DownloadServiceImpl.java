package ru.royal.fileExchanger.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    @Autowired
    public DownloadServiceImpl(DownloadRepository downloadRepository, LinkRepository linkRepository) {
        this.downloadRepository = downloadRepository;
        this.linkRepository = linkRepository;
    }

    @Override
    public void saveDownload(Link link) {
        Download download = new Download();
        User user = linkRepository.findUserByLinkId(link.getId())
                .orElseThrow(() -> new IllegalArgumentException("Пользователь для данной ссылки не найден"));

        download.setDownloadAt(Timestamp.valueOf(LocalDateTime.now()));
        download.setDownloadIp("ip");
        download.setUser(user);
        download.setLink(link);

        downloadRepository.save(download);
    }
}
