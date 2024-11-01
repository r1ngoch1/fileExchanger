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
import ru.royal.fileExchanger.repository.UserRepository;

import java.util.List;


@Service
public class FileServiceImpl implements FileService{

    private final FileRepository fileRepository;
    private final LinkRepository linkRepository;
    private final UserRepository userRepository;
    private final LinkService linkService;

    @Autowired
    public FileServiceImpl(FileRepository fileRepository, LinkRepository linkRepository,
                           UserRepository userRepository, LinkService linkService) {
        this.fileRepository = fileRepository;
        this.linkRepository = linkRepository;
        this.userRepository = userRepository;
        this.linkService = linkService;
    }

    @Override
    @Transactional
    public void deleteFile(String filename) {
        linkService.deleteAllByFilename(filename);
        fileRepository.deleteByFileName(filename);
    }


    @Override
    @Transactional
    public void deleteFilesByUser(String username) {
        linkService.deleteAllByUsername(username);
        fileRepository.deleteAllByUser(userRepository.findByUsername(username));
    }
}