package ru.royal.fileExchanger;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.royal.fileExchanger.entities.File;
import ru.royal.fileExchanger.entities.Link;
import ru.royal.fileExchanger.entities.User;
import ru.royal.fileExchanger.repository.FileRepository;
import ru.royal.fileExchanger.repository.LinkRepository;
import ru.royal.fileExchanger.repository.UserRepository;
import ru.royal.fileExchanger.service.FileService;


import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Transactional
@SpringBootTest
public class FileTest {
    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final FileService fileService;
    private final LinkRepository linkRepository;

    @Autowired
    public FileTest(final FileRepository fileRepository, final UserRepository userRepository, final FileService fileService, LinkRepository linkRepository) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
        this.fileService = fileService;
        this.linkRepository = linkRepository;
    }

    @Test
    void testCreateUser(){
        File file = new File();
        User user  = userRepository.findByUsernameAndPassword("ivan", "14235123");
        file.setFileName("ytry");
        file.setFileType(".rtf");
        file.setUploadedAt(new Timestamp(124));
        file.setStoragePath("/dfgdfg/gsdf");
        file.setUser(user);
        fileRepository.save(file);
    }
    @Test
    void testFindFileByUsername(){
        List<File> files = fileRepository.findByUsername("ivan");

    }


    @Test
    void testCreateFile(){
        File file = new File();

        file.setFileName("eytryetr");
        file.setFileName("fsdf");
        file.setStoragePath("dsfsdf");
        file.setUploadedAt(Timestamp.valueOf(LocalDateTime.now()));
        file.setUser(userRepository.findById(852L).get());
        fileRepository.save(file);
        Assertions.assertNotNull(file);

    }


}
