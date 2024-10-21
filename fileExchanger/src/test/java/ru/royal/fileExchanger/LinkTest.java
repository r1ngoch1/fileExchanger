package ru.royal.fileExchanger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.royal.fileExchanger.entities.File;
import ru.royal.fileExchanger.entities.Link;
import ru.royal.fileExchanger.repository.FileRepository;
import ru.royal.fileExchanger.repository.LinkRepository;

@SpringBootTest
public class LinkTest {
    private final LinkRepository linkRepository;
    private final FileRepository fileRepository;
    @Autowired
    public LinkTest(final LinkRepository linkRepository, final FileRepository fileRepository) {
        this.linkRepository = linkRepository;
        this.fileRepository = fileRepository;
    }
    @Test
    void testCreateLink(){
        Link link = new Link();
        File file = fileRepository.findByFileName("doc").getFirst();
        link.setFile(file);
        link.setLinkHash("f5345");
        linkRepository.save(link);
        Assertions.assertNotNull(link);
    }

}
