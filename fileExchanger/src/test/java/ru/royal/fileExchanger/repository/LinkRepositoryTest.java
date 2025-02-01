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

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class LinkRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private LinkRepository linkRepository;

    private User testUser;
    private File testFile;
    private Directory testDirectory;
    private Link testLink;
    private Link testLink2;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testUser");
        entityManager.persist(testUser);

        testDirectory = new Directory();
        testDirectory.setName("TestDir");
        testDirectory.setUser(testUser);
        testDirectory.setS3Path("/test/dir");
        entityManager.persist(testDirectory);


        testFile = new File();
        testFile.setFileName("document.pdf");
        testFile.setUser(testUser);
        testFile.setDirectory(testDirectory);
        entityManager.persist(testFile);

        testLink = new Link();
        testLink.setLinkHash("hash123");
        testLink.setFile(testFile);
        testLink.setDirectory(testDirectory);
        entityManager.persist(testLink);

        testLink2 = new Link();
        testLink2.setLinkHash("hash456");
        testLink2.setFile(testFile);
        entityManager.persist(testLink2);

        entityManager.flush();
    }

    @Test
    void findByFileName_ShouldReturnLinksForFile() {
        List<Link> links = linkRepository.findByFileName("document.pdf");
        assertThat(links)
                .hasSize(2)
                .allMatch(link -> link.getFile().getFileName().equals("document.pdf"));
    }

    @Test
    void findByUsername_ShouldReturnUserLinks() {
        List<Link> links = linkRepository.findByUsername("testUser");
        assertThat(links)
                .hasSize(2)
                .allMatch(link -> link.getFile().getUser().getUsername().equals("testUser"));
    }

    @Test
    void findByFileId_ShouldReturnLinksForSpecificFile() {
        List<Link> links = linkRepository.findByFileId(testFile.getId());
        assertThat(links)
                .hasSize(2)
                .allMatch(link -> link.getFile().getId().equals(testFile.getId()));
    }

    @Test
    void findLinkByLinkHash_ShouldReturnCorrectLink() {
        Link found = linkRepository.findLinkByLinkHash("hash123");
        assertThat(found).isNotNull();
        assertThat(found.getFile().getFileName()).isEqualTo("document.pdf");
    }

    @Test
    void findFileByLinkHash_ShouldReturnRelatedFile() {
        File file = linkRepository.findFileByLinkHash("hash123");
        assertThat(file).isNotNull();
        assertThat(file.getFileName()).isEqualTo("document.pdf");
    }

    @Test
    void findDirectoryByLinkHash_ShouldReturnDirectoryIfExists() {
        Optional<Directory> directory = linkRepository.findDirectoryByLinkHash("hash123");
        assertThat(directory).isPresent();
        assertThat(directory.get().getName()).isEqualTo("TestDir");
    }

    @Test
    void findDirectoryByLinkHash_ShouldReturnEmptyForLinkWithoutDirectory() {
        Optional<Directory> directory = linkRepository.findDirectoryByLinkHash("hash456");
        assertThat(directory).isEmpty();
    }

    @Test
    void findUserByLinkId_ShouldReturnCorrectUser() {
        Optional<User> user = linkRepository.findUserByLinkId(testLink.getId());
        assertThat(user)
                .isPresent()
                .get()
                .extracting(User::getUsername)
                .isEqualTo("testUser");
    }

    @Test
    void findUserByLinkId_ShouldReturnEmptyForInvalidId() {
        Optional<User> user = linkRepository.findUserByLinkId(999L);
        assertThat(user).isEmpty();
    }
}
