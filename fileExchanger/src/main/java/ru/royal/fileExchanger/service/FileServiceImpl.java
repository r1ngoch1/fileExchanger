package ru.royal.fileExchanger.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import ru.royal.fileExchanger.entities.Link;
import ru.royal.fileExchanger.repository.FileRepository;
import ru.royal.fileExchanger.repository.LinkRepository;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;


@Service
public class FileServiceImpl implements FileService{

    private final FileRepository fileRepository;
    private final LinkRepository linkRepository;

    @Autowired
    public FileServiceImpl(FileRepository fileRepository, LinkRepository linkRepository) {
        this.fileRepository = fileRepository;
        this.linkRepository = linkRepository;
    }

    @Override
    @Transactional
    public void deleteFile(String filename) {
        List<Link> links = linkRepository.findByFileName(filename);
        linkRepository.deleteAll(links);
        fileRepository.deleteByFileName(filename);
    }
}