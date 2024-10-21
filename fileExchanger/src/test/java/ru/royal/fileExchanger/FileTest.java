package ru.royal.fileExchanger;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.royal.fileExchanger.dao.FileRepositoryImpl;
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
        file.setFileSize(BigInteger.valueOf(1234324));
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
    void testDeleteFile(){
        File file = new File();
        file.setFileName("apc");
        fileRepository.save(file);

        Link link1 = new Link();
        link1.setFile(file);
        Link link2 = new Link();
        link2.setFile(file);
        linkRepository.save(link1);
        linkRepository.save(link2);

        fileService.deleteFile(file.getFileName());

        Optional<File> foundFile = fileRepository.findById(file.getId());
        Assertions.assertTrue(foundFile.isEmpty());

        Optional<Link> foundLink1 = linkRepository.findById(link1.getId());
        Assertions.assertTrue(foundLink1.isEmpty());

        Optional<Link> foundLink2 = linkRepository.findById(link2.getId());
        Assertions.assertTrue(foundLink2.isEmpty());

    }
    @Test
    void deleteFileAgain(){
        Optional<File> file = fileRepository.findById(Long.valueOf(1));
        Assertions.assertTrue(file.isPresent(), "File with ID 1 should exist");

        fileService.deleteFile(file.get().getFileName());

        Optional<File> deletedFile = fileRepository.findById(file.get().getId());
        Assertions.assertTrue(deletedFile.isEmpty(), "File should have been deleted");
    }
    @Test
    void testCreateFile(){
        File file = new File();

        file.setFileName("eytryetr");
        file.setFileName("fsdf");
        file.setStoragePath("dsfsdf");
        file.setFileSize(BigInteger.valueOf(5235));
        file.setUploadedAt(Timestamp.valueOf(LocalDateTime.now()));
        file.setUser(userRepository.findByUsernameAndPassword("royal","14235123"));
        fileRepository.save(file);
        Assertions.assertNotNull(file);

    }


}
