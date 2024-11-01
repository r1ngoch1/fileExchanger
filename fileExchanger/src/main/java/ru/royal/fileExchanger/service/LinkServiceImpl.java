package ru.royal.fileExchanger.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.royal.fileExchanger.entities.File;
import ru.royal.fileExchanger.entities.Link;
import ru.royal.fileExchanger.repository.LinkRepository;

import java.util.List;

@Service
public class LinkServiceImpl implements LinkService{
    private final LinkRepository linkRepository;

    @Autowired
    public LinkServiceImpl(LinkRepository linkRepository) {
        this.linkRepository = linkRepository;
    }

    @Override
    public void deleteAllByUsername(String username) {
        List<Link> links = linkRepository.findByUsername(username);
        linkRepository.deleteAll(links);
    }

    @Override
    public void deleteAllByFilename(String filename) {
        List<Link> links = linkRepository.findByFileName(filename);
        linkRepository.deleteAll(links);
    }
}
