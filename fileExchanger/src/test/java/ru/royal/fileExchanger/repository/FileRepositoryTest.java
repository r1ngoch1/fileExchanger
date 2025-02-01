package ru.royal.fileExchanger.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import ru.royal.fileExchanger.entities.Directory;
import ru.royal.fileExchanger.entities.File;
import ru.royal.fileExchanger.entities.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class FileRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FileRepository fileRepository;


    private User testUser;
    private File testFile;
    private File testFile2;
    private Directory testDirectory;
    private File rootFile;

    @BeforeEach
    void setUp() {
        // Создаем тестового пользователя
        testUser = new User();
        testUser.setUsername("testUser");
        entityManager.persist(testUser);

        // Создаем корневую директорию (id = 0)
        Directory rootDirectory = entityManager.find(Directory.class, 0L);
        if (rootDirectory == null) {
            rootDirectory = new Directory();
            rootDirectory.setId(0L);
            rootDirectory.setName("root");
            rootDirectory.setUser(testUser);
            rootDirectory.setS3Path("/root");
            rootDirectory.setActive(true);
            rootDirectory = entityManager.merge(rootDirectory); // используем merge вместо persist
        }
        // Создаем "корневой" файл (в директории с id = 0)
        rootFile = new File();
        rootFile.setFileName("root.txt");
        rootFile.setUser(testUser);
        rootFile.setActive(true);
        rootFile.setStoragePath("/storage/root.txt");
        rootFile.setDirectory(rootDirectory); // привязываем к корневой директории
        entityManager.persist(rootFile);

        testDirectory = new Directory();
        testDirectory.setName("testDirectory");
        testDirectory.setS3Path("/storage/test.txt");
        testDirectory.setUser(testUser);
        entityManager.persist(testDirectory);

        // Создаем файл в директории
        testFile = new File();
        testFile.setFileName("test.txt");
        testFile.setUser(testUser);
        testFile.setActive(true);
        testFile.setStoragePath("/storage/test.txt");
        testFile.setDirectory(testDirectory);
        entityManager.persist(testFile);

        // Создаем неактивный файл
        testFile2 = new File();
        testFile2.setFileName("another.txt");
        testFile2.setUser(testUser);
        testFile2.setActive(false);
        testFile2.setDirectory(testDirectory);
        entityManager.persist(testFile2);

        // Создаем корневой файл (без директории)
        rootFile = new File();
        rootFile.setFileName("root.txt");
        rootFile.setUser(testUser);
        rootFile.setActive(true);
        rootFile.setStoragePath("/storage/root.txt");
        rootFile.setDirectory(null);
        entityManager.persist(rootFile);

        entityManager.flush();
    }

    @Test
    void findByFileName_ShouldReturnFilesWithGivenName() {
        List<File> found = fileRepository.findByFileName("test.txt");
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getStoragePath()).isEqualTo("/storage/test.txt");
    }

    @Test
    void findByUsername_ShouldReturnActiveFilesForUser() {
        List<File> files = fileRepository.findByUsername("testUser");
        assertThat(files)
                .hasSize(3)
                .allMatch(File::isActive)
                .allMatch(f -> f.getUser().getUsername().equals("testUser"));
    }

    @Test
    void deleteByFileName_ShouldRemoveFiles() {
        fileRepository.deleteByFileName("test.txt");
        assertThat(fileRepository.findByFileName("test.txt")).isEmpty();
    }

    @Test
    void deleteAllByUser_ShouldRemoveAllUserFiles() {
        fileRepository.deleteAllByUser(testUser);
        assertThat(fileRepository.findByUsername("testUser")).isEmpty();
    }

    @Test
    void deleteById_ShouldRemoveFile() {
        fileRepository.deleteById(testFile.getId());
        assertThat(fileRepository.findById(testFile.getId())).isEmpty();
    }

    @Test
    void getFileById_ShouldReturnCorrectFile() {
        File found = fileRepository.getFileById(testFile.getId());
        assertThat(found.getFileName()).isEqualTo("test.txt");
    }


    @Test
    void findByStoragePath_ShouldReturnFile() {
        Optional<File> found = fileRepository.findByStoragePath("/storage/test.txt");
        assertThat(found).isPresent();
        assertThat(found.get().getFileName()).isEqualTo("test.txt");
    }

    @Test
    void findByStoragePath_ShouldReturnEmptyForNonExistingPath() {
        Optional<File> found = fileRepository.findByStoragePath("/wrong/path");
        assertThat(found).isEmpty();
    }
}
