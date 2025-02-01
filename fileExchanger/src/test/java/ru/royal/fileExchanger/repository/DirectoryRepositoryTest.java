package ru.royal.fileExchanger.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import ru.royal.fileExchanger.entities.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class DirectoryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DirectoryRepository directoryRepository;

    private User testUser;
    private Directory rootDir;
    private Directory subDir;
    private File testFile;
    private Link testLink;
    private String linkHash;

    @BeforeEach
    void setUp() {
        // Создаем пользователя
        testUser = new User();
        testUser.setUsername("testUser");
        testUser = entityManager.persist(testUser);  // Сохраняем и получаем обновленную сущность

        // Создаем корневую директорию
        rootDir = new Directory();
        rootDir.setName("Root");
        rootDir.setUser(testUser);
        rootDir.setParentDirectory(null);
        rootDir.setS3Path("/root/" + UUID.randomUUID());  // Генерируем уникальный путь
        rootDir.setActive(true);
        rootDir = entityManager.persist(rootDir);  // Сохраняем и получаем обновленную сущность

        // Создаем поддиректорию
        subDir = new Directory();
        subDir.setName("Sub");
        subDir.setUser(testUser);
        subDir.setParentDirectory(rootDir);
        subDir.setS3Path("/root/" + rootDir.getId() + "/sub/" + UUID.randomUUID());  // Генерируем уникальный путь
        subDir.setActive(true);
        subDir = entityManager.persist(subDir);

        // Создаем тестовый файл
        testFile = new File();
        testFile.setFileName("test.txt");
        testFile.setDirectory(rootDir);
        testFile.setActive(true);
        testFile = entityManager.persist(testFile);

        // Создаем тестовую ссылку
        testLink = new Link();
        linkHash = UUID.randomUUID().toString();
        testLink.setLinkHash(linkHash);
        testLink.setDirectory(rootDir);
        testLink = entityManager.persist(testLink);

        entityManager.flush();
    }
    @Test
    void findById_ShouldReturnDirectory() {
        Optional<Directory> found = directoryRepository.findById(rootDir.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Root");
    }

    @Test
    void findAllByDirectoryId_ShouldReturnActiveFiles() {
        List<File> files = directoryRepository.findAllByDirectoryId(rootDir.getId());
        assertThat(files).hasSize(1);
        assertThat(files.get(0).getFileName()).isEqualTo("test.txt");
    }

    @Test
    void findSubdirectoriesByDirectoryId_ShouldReturnActiveSubdirs() {
        List<Directory> subdirs = directoryRepository.findSubdirectoriesByDirectoryId(rootDir.getId());
        assertThat(subdirs).hasSize(1);
        assertThat(subdirs.get(0).getName()).isEqualTo("Sub");
    }

    @Test
    void findDirectoriesByUser_ShouldReturnUserDirs() {
        List<Directory> directories = directoryRepository.findDirectoriesByUser(testUser);
        assertThat(directories).hasSize(2);
    }

    @Test
    void findLinksByDirectoryId_ShouldReturnLinks() {
        List<Link> links = directoryRepository.findLinksByDirectoryId(rootDir.getId());
        assertThat(links).hasSize(1);
        assertThat(links.get(0).getLinkHash()).isEqualTo(linkHash);
    }
}