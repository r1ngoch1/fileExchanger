package ru.royal.fileExchanger.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import ru.royal.fileExchanger.SecurityUtils;
import ru.royal.fileExchanger.entities.*;
import ru.royal.fileExchanger.repository.DirectoryRepository;
import ru.royal.fileExchanger.repository.FileRepository;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.http.AbortableInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @Mock
    private FileRepository fileRepository;

    @Mock
    private DirectoryRepository directoryRepository;

    @Mock
    private S3Client s3Client;

    @Mock
    private SecurityUtils securityUtils;

    @Mock
    private LinkService linkService;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private FileServiceImpl fileService;

    private final String testBucket = "fileexchanger";

    @Test
    void uploadFile_ShouldSaveFileToS3AndDatabase() throws IOException {
        // Arrange
        Directory directory = new Directory();
        directory.setId(1L);
        directory.setS3Path("/test/");

        User user = new User();
        user.setUsername("testUser");

        String fileName = "test.txt";
        byte[] content = "content".getBytes();
        long fileSize = 1024L;

        when(multipartFile.getOriginalFilename()).thenReturn(fileName);
        when(multipartFile.getSize()).thenReturn(fileSize);
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(content));
        when(multipartFile.getContentType()).thenReturn("text/plain");
        when(directoryRepository.findById(any(Long.class))).thenReturn(Optional.of(directory));
        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(fileRepository.save(any(File.class))).thenAnswer(i -> i.getArgument(0));

        File result = fileService.uploadFile(multipartFile, 1L);

        verify(s3Client).putObject(
                argThat((PutObjectRequest request) -> {
                    return request.bucket().equals(testBucket) &&
                            request.key().startsWith("/test/") &&
                            request.contentLength() == fileSize &&
                            request.contentType().equals("text/plain");
                }),
                argThat((RequestBody requestBody) -> {
                    try {
                        return requestBody.contentLength() == fileSize;
                    } catch (Exception e) {
                        return false;
                    }
                })
        );

        assertNotNull(result);
        assertTrue(result.getStoragePath().startsWith("/test/"));
        assertEquals(fileName, result.getFileName());
        assertEquals("txt", result.getFileType());
        assertTrue(result.isActive());
    }

    @Test
    void downloadFile_ShouldReturnResourceFromS3() {
        String fileKey = "/test/file.txt";
        byte[] content = "test content".getBytes();

        GetObjectRequest expectedRequest = GetObjectRequest.builder()
                .bucket(testBucket)
                .key(fileKey)
                .build();

        doAnswer(invocation -> {
            ResponseTransformer<GetObjectResponse, ?> transformer = invocation.getArgument(1);
            return transformer.transform(
                    GetObjectResponse.builder()
                            .contentLength((long) content.length)
                            .build(),
                    AbortableInputStream.create(new ByteArrayInputStream(content))
            );
        }).when(s3Client).getObject(eq(expectedRequest), any(ResponseTransformer.class));

        Resource resource = fileService.downloadFile(fileKey);

        verify(s3Client).getObject(eq(expectedRequest), any(ResponseTransformer.class));
        assertNotNull(resource);
    }

    @Test
    void deleteFile_ShouldDeactivateAndCleanResources() {
        File file = new File();
        file.setId(1L);
        file.setStoragePath("/test/file.txt");
        file.setActive(true);

        when(fileRepository.findById(1L)).thenReturn(Optional.of(file));

        fileService.deleteFile(1L);

        verify(s3Client).deleteObject(
                DeleteObjectRequest.builder()
                        .bucket(testBucket)
                        .key("/test/file.txt")
                        .build()
        );
        verify(linkService).updateAllActiveByFile(file);
        assertFalse(file.isActive());
        verify(fileRepository).save(file);
    }

    @Test
    void getFilesInDirectory_ShouldReturnFilesFromRepository() {
        List<File> expectedFiles = List.of(new File(), new File());
        when(directoryRepository.findAllByDirectoryId(1L)).thenReturn(expectedFiles);

        List<File> result = fileService.getFilesInDirectory(1L);

        assertEquals(2, result.size());
    }

    @Test
    void updateActivityFilesByUser_ShouldDeactivateAllFiles() {
        List<File> files = List.of(new File(), new File());
        when(fileRepository.findByUsername("testUser")).thenReturn(files);

        fileService.updateActivityFilesByUser("testUser");

        verify(linkService).updateAllActiveByUsername("testUser");
        files.forEach(file -> {
            assertFalse(file.isActive());
            verify(fileRepository).save(file);
        });
    }

    @Test
    void getFilesInRootDirectory_ShouldReturnRootFiles() {
        User user = new User();
        List<File> expectedFiles = List.of(new File());
        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(fileRepository.findAllByUserAndDirectoryIsNull(user)).thenReturn(expectedFiles);

        List<File> result = fileService.getFilesInRootDirectory();

        assertEquals(1, result.size());
    }

    @Test
    void findFileById_ShouldReturnFileFromRepository() {
        File expected = new File();
        when(fileRepository.getFileById(1L)).thenReturn(expected);

        File result = fileService.findFileById(1L);

        assertEquals(expected, result);
    }
}