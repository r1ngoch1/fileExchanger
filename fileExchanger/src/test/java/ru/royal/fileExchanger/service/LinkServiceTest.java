package ru.royal.fileExchanger.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.royal.fileExchanger.entities.*;
import ru.royal.fileExchanger.repository.DirectoryRepository;
import ru.royal.fileExchanger.repository.FileRepository;
import ru.royal.fileExchanger.repository.LinkRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LinkServiceTest {

    @Mock
    private LinkRepository linkRepository;

    @Mock
    private FileRepository fileRepository;

    @Mock
    private DirectoryRepository directoryRepository;

    @InjectMocks
    private LinkServiceImpl linkService;

    @Test
    void updateAllActiveByUsername_ShouldDeactivateAllLinks() {
        Link link = new Link();
        link.setIsActive(true);

        when(linkRepository.findByUsername("testUser"))
                .thenReturn(List.of(link));

        linkService.updateAllActiveByUsername("testUser");

        assertFalse(link.getIsActive());
        verify(linkRepository).save(link);
    }

    @Test
    void updateAllActiveByFile_ShouldDeactivateFileLinks() {
        File file = new File();
        file.setId(1L);
        Link link = new Link();
        link.setIsActive(true);

        when(linkRepository.findByFileId(1L)).thenReturn(List.of(link));

        linkService.updateAllActiveByFile(file);

        assertFalse(link.getIsActive());
        verify(linkRepository).save(link);
    }

    @Test
    void deleteAllByFilename_ShouldRemoveLinks() {
        when(linkRepository.findByFileName("test.txt"))
                .thenReturn(List.of(new Link(), new Link()));

        linkService.deleteAllByFilename("test.txt");

        verify(linkRepository).deleteAll(anyList());
    }

    @Test
    void createLink_ForFile_ShouldCreateValidLink() {
        File file = new File();
        file.setId(3L);
        when(fileRepository.findById(3L)).thenReturn(Optional.of(file));

        Link result = linkService.createLink(3L, 24, false);

        assertNotNull(result.getLinkHash());
        assertEquals(file, result.getFile());
        assertTrue(result.getIsActive());
        verify(linkRepository).save(result);
    }

    @Test
    void createLink_ForDirectory_ShouldCreateValidLink() {
        Directory directory = new Directory();
        directory.setId(4L);
        directory.setActive(true);

        when(directoryRepository.findById(4L))
                .thenReturn(Optional.of(directory));

        Link expectedLink = new Link();
        expectedLink.setDirectory(directory);
        expectedLink.setIsActive(true);
        when(linkRepository.save(any(Link.class)))
                .thenReturn(expectedLink);

        Link result = linkService.createLink(4L, 0, true);

        assertNotNull(result);
        assertEquals(directory, result.getDirectory());
        assertTrue(result.getIsActive());

        verify(directoryRepository).findById(4L);
        verify(linkRepository).save(any(Link.class));
    }

    @Test
    void getFileByHash_ShouldThrowWhenLinkExpired() {
        Link link = new Link();
        link.setExpirationDate(Timestamp.from(Instant.now().minusSeconds(3600)));
        link.setIsActive(true);

        when(linkRepository.findLinkByLinkHash("expired")).thenReturn(link);

        assertThrows(IllegalStateException.class,
                () -> linkService.getFileByHash("expired"));
    }


    @Test
    void getLinkByHash_ShouldReturnLink() {
        Link expected = new Link();
        when(linkRepository.findLinkByLinkHash("hash123")).thenReturn(expected);

        Link result = linkService.getLinkByHash("hash123");

        assertEquals(expected, result);
    }

    @Test
    void deleteByFileId_ShouldRemoveAllLinksForFile() {
        when(linkRepository.findByFileId(1L))
                .thenReturn(List.of(new Link(), new Link()));

        linkService.deleteByFileId(1L);

        verify(linkRepository).deleteAll(anyList());
    }
}
