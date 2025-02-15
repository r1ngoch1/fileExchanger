package ru.royal.fileExchanger.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.royal.fileExchanger.entities.Directory;
import ru.royal.fileExchanger.repository.DirectoryRepository;
import ru.royal.fileExchanger.repository.UserRepository;

import java.util.Optional;

@Service
public class DirectoryInitService {

    private final DirectoryRepository directoryRepository;
    private final UserRepository userRepository; // Если директория привязана к пользователю

    @Autowired
    public DirectoryInitService(DirectoryRepository directoryRepository, UserRepository userRepository) {
        this.directoryRepository = directoryRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void createRootDirectoryIfNotExists() {
        Optional<Directory> rootDir = directoryRepository.findByIsRoot(true);

        if (rootDir.isEmpty()) {
            Directory directory = new Directory();
            directory.setName("ROOT");
            directory.setS3Path("root/");
            directory.setActive(true);
            directory.setRoot(true);
            directory.setParentDirectory(null);

            directoryRepository.save(directory);
            System.out.println("Создана главная директория ROOT");
        }
    }

}
