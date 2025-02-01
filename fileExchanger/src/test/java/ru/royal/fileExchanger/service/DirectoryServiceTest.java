package ru.royal.fileExchanger.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import ru.royal.fileExchanger.SecurityUtils;
import ru.royal.fileExchanger.entities.*;
import ru.royal.fileExchanger.repository.*;

import java.util.*;


import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DirectoryServiceTest {

    @Mock
    private DirectoryRepository directoryRepository;

    @Mock
    private SecurityUtils securityUtils;

    @Mock
    private FileRepository fileRepository;

    @Mock
    private S3Client s3Client;

    @Mock
    private LinkRepository linkRepository;

    @InjectMocks
    private DirectoryServiceImpl directoryService;

    private final String testBucket = "fileexchanger";

    @Test
    void createDirectory_ShouldCreateWithParent() {
        User user = new User();
        Directory parent = new Directory();
        parent.setId(1L);
        parent.setS3Path("/parent");

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(directoryRepository.findById(any(Long.class))).thenReturn(Optional.of(parent));
        when(directoryRepository.save(any(Directory.class))).thenAnswer(inv -> inv.getArgument(0));

        Directory result = directoryService.createDirectory("child", 1L);

        assertEquals("child", result.getName());
        assertEquals("/parent/child", result.getS3Path());
        assertTrue(result.isActive());
        assertEquals(user, result.getUser());
    }


    @Test
    void deleteDirectory_ShouldDeactivateAndCleanResources() {
        Directory directory = new Directory();
        List<File> files = new ArrayList<>();
        directory.setId(1L);
        directory.setS3Path("/test/path");
        directory.setFiles(files);

        ListObjectsV2Response listResponse = ListObjectsV2Response.builder()
                .contents(new ArrayList<>())
                .build();

        when(directoryRepository.findById(any(Long.class))).thenReturn(Optional.of(directory));
        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(listResponse);
        when(directoryRepository.findLinksByDirectoryId(any(Long.class))).thenReturn(new ArrayList<>());
        when(directoryRepository.save(any(Directory.class))).thenReturn(directory);

        directoryService.deleteDirectory(1L);

        verify(directoryRepository).save(argThat(dir -> !dir.isActive()));
        verify(s3Client).listObjectsV2(any(ListObjectsV2Request.class));
    }

    @Test
    void downloadDirectoryAsZip_ShouldCreateValidZip() throws IOException {
        Directory dir = new Directory();
        dir.setS3Path("/test");
        dir.setName("test");

        S3Object s3Object = S3Object.builder().key("/test/file.txt").build();
        File fileEntity = new File();
        fileEntity.setFileName("original.txt");

        when(linkRepository.findDirectoryByLinkHash("hash")).thenReturn(Optional.of(dir));
        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class)))
                .thenReturn(ListObjectsV2Response.builder().contents(s3Object).build());
        when(fileRepository.findByStoragePath("/test/file.txt")).thenReturn(Optional.of(fileEntity));

        directoryService.downloadDirectoryAsZip("hash", "test.zip");

        verify(s3Client).getObject(any(GetObjectRequest.class), (ResponseTransformer<GetObjectResponse, Object>) any());
    }

    @Test
    void getRootDirectoriesByUser_ShouldReturnRootDirs() {
        User user = new User();
        Directory rootDir = new Directory();

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(directoryRepository.findRootDirectories(user)).thenReturn(Collections.singletonList(rootDir));

        List<Directory> result = directoryService.getRootDirectoriesByUser();

        assertEquals(1, result.size());
        assertEquals(rootDir, result.get(0));
    }

    @Test
    void getDirectoryById_ShouldThrowWhenNotFound() {
        doReturn(Optional.empty())
                .when(directoryRepository)
                .findById(any(Long.class));

        assertThrows(IllegalArgumentException.class,
                () -> directoryService.getDirectoryById(999L));
    }
}