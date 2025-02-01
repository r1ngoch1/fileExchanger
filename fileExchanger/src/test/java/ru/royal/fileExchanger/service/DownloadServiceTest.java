package ru.royal.fileExchanger.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.royal.fileExchanger.entities.Download;
import ru.royal.fileExchanger.entities.Link;
import ru.royal.fileExchanger.entities.User;
import ru.royal.fileExchanger.repository.DownloadRepository;
import ru.royal.fileExchanger.repository.LinkRepository;
import ru.royal.fileExchanger.SecurityUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DownloadServiceTest {

    @Mock
    private DownloadRepository downloadRepository;

    @Mock
    private LinkRepository linkRepository;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private DownloadServiceImpl downloadService;

    @Test
    void saveDownload_ShouldCreateAndSaveEntityCorrectly() {
        User testUser = new User();
        testUser.setUsername("testUser");

        Link testLink = new Link();
        testLink.setId(1L);

        when(securityUtils.getCurrentUser()).thenReturn(testUser);

        ArgumentCaptor<Download> downloadCaptor = ArgumentCaptor.forClass(Download.class);

        downloadService.saveDownload(testLink);

        verify(downloadRepository).save(downloadCaptor.capture());

        Download savedDownload = downloadCaptor.getValue();
        assertNotNull(savedDownload.getDownloadAt());
        assertEquals("ip", savedDownload.getDownloadIp());
        assertEquals(testUser, savedDownload.getUser());
        assertEquals(testLink, savedDownload.getLink());
    }

    @Test
    void saveDownload_ShouldHandleNullUserGracefully() {
        Link testLink = new Link();
        when(securityUtils.getCurrentUser()).thenReturn(null);

        assertDoesNotThrow(() -> downloadService.saveDownload(testLink));
        verify(downloadRepository).save(any(Download.class));
    }

}