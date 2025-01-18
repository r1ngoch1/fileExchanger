package ru.royal.fileExchanger.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.royal.fileExchanger.SecurityUtils;
import ru.royal.fileExchanger.entities.Directory;
import ru.royal.fileExchanger.entities.User;
import ru.royal.fileExchanger.repository.DirectoryRepository;
import ru.royal.fileExchanger.repository.UserRepository;

import java.util.List;

@Service
public class DirectoryServiceImpl implements DirectoryService {

    private final DirectoryRepository directoryRepository;
    private final SecurityUtils securityUtils;


    @Autowired
    public DirectoryServiceImpl(DirectoryRepository directoryRepository, SecurityUtils securityUtils) {
        this.directoryRepository = directoryRepository;
        this.securityUtils = securityUtils;
    }

    @Override
    public Directory createDirectory(String name, Long parentDirectoryId) {
        User currentUser = securityUtils.getCurrentUser();
        Directory parentDirectory = null;

        if (parentDirectoryId != null) {
            parentDirectory = directoryRepository.findById(parentDirectoryId)
                    .orElseThrow(() -> new IllegalArgumentException("Родительская директория не найдена"));
        }

        String s3Path;
        if (parentDirectoryId != 0L) {
            s3Path = parentDirectory.getS3Path() + "/" + name; // Добавляем в путь имя директории
        } else {
            assert parentDirectory != null;
            s3Path = parentDirectory.getS3Path() + name; // Для корневой директории
        }

        Directory newDirectory = new Directory();
        newDirectory.setName(name);
        newDirectory.setUser(currentUser);
        newDirectory.setParentDirectory(parentDirectory);
        newDirectory.setS3Path(s3Path);


        return directoryRepository.save(newDirectory);
    }

    @Override
    public List<Directory> getSubdirectories(Long directoryId) {
        return directoryRepository.findSubdirectoriesByDirectoryId(directoryId);
    }

    @Override
    public List<Directory> getDirectoriesByUser() {
        User currentUser = securityUtils.getCurrentUser();
        return directoryRepository.findDirectoriesByUser(currentUser);
    }

    @Override
    public Directory getDirectoryById(Long directoryId) {
        return directoryRepository.findById(directoryId)
                .orElseThrow(() -> new IllegalArgumentException("Директория с ID " + directoryId + " не найдена."));
    }


    @Override
    public List<Directory> getRootDirectoriesByUser() {
        User currentUser = securityUtils.getCurrentUser();
        return directoryRepository.findRootDirByDirectoryId(currentUser);
    }


}
