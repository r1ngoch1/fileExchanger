package ru.royal.fileExchanger.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.royal.fileExchanger.SecurityUtils;
import ru.royal.fileExchanger.entities.Directory;
import ru.royal.fileExchanger.entities.File;
import ru.royal.fileExchanger.entities.Link;
import ru.royal.fileExchanger.entities.User;
import ru.royal.fileExchanger.repository.DirectoryRepository;
import ru.royal.fileExchanger.repository.FileRepository;
import ru.royal.fileExchanger.repository.LinkRepository;
import ru.royal.fileExchanger.repository.UserRepository;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import software.amazon.awssdk.core.sync.ResponseTransformer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static ru.royal.fileExchanger.Utils.extractKeyFromUrl;


@Service
public class  FileServiceImpl implements FileService{

    private final FileRepository fileRepository;
    private final LinkRepository linkRepository;
    private final UserRepository userRepository;
    private final LinkService linkService;
    private final S3Client s3Client;
    private final SecurityUtils securityUtils;
    private final DirectoryRepository directoryRepository;

    private final String bucketName = "fileexchanger";

    @Autowired
    public FileServiceImpl(FileRepository fileRepository, LinkRepository linkRepository,
                           UserRepository userRepository, LinkService linkService,
                           S3Client s3Client, SecurityUtils securityUtils, DirectoryRepository directoryRepository) {
        this.fileRepository = fileRepository;
        this.linkRepository = linkRepository;
        this.userRepository = userRepository;
        this.linkService = linkService;
        this.s3Client = s3Client;
        this.securityUtils = securityUtils;
        this.directoryRepository = directoryRepository;
    }

    public File uploadFile(MultipartFile file, Long directoryId) throws IOException {
        try {
            // Если ID директории не указан, используем корневую директорию
            Directory directory = null;
            if (directoryId == null) {
                directoryId = 0L; // ID виртуальной корневой директории
            }

            directory = directoryRepository.findById(directoryId)
                    .orElseThrow(() -> new IllegalArgumentException("Директория не найдена"));

            // Генерация уникального имени файла
            String uniqueFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

            // Если файл привязан к директории, добавляем имя директории к пути
            String s3Key = directory.getS3Path() +"/"+ uniqueFileName;

            // Загрузка файла в S3
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(s3Key)
                            .contentType(file.getContentType()) // Добавляем тип контента
                            .contentLength(file.getSize())      // Добавляем размер файла
                            .build(),
                    software.amazon.awssdk.core.sync.RequestBody.fromInputStream(
                            file.getInputStream(),    // Используем InputStream вместо getBytes()
                            file.getSize()
                    )
            );
            System.out.println("Файл загружен в S3: " + s3Key);

            // Создание объекта File
            File fileBd = new File();
            fileBd.setUploadedAt(Timestamp.valueOf(LocalDateTime.now()));
            fileBd.setStoragePath(s3Key);
            fileBd.setFileSize(file.getSize()/1024);
            fileBd.setFileName(file.getOriginalFilename());
            fileBd.setFileType(getFileExtension(Objects.requireNonNull(file.getOriginalFilename())));
            fileBd.setUser(securityUtils.getCurrentUser());
            fileBd.setDirectory(directory);
            fileBd.setActive(true);

            // Сохранение в базу данных
            File savedFile = fileRepository.save(fileBd);
            System.out.println("Файл сохранен в базе данных: " + savedFile.getId());

            return savedFile;

        } catch (Exception e) {
            System.err.println("Ошибка при загрузке файла: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Ошибка при загрузке файла", e);
        }
    }



    public String generateFileUrl(String fileName) {
        return s3Client.utilities().getUrl(
                GetUrlRequest.builder()
                        .bucket(bucketName)
                        .key(fileName)
                        .build()
        ).toString();
    }

    @Override
    public Resource downloadFile(String fileKey) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            GetObjectResponse response = s3Client.getObject(
                    GetObjectRequest.builder()
                            .bucket(bucketName)
                            .key(fileKey)
                            .build(),
                    ResponseTransformer.toOutputStream(outputStream)
            );

            return new ByteArrayResource(outputStream.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при загрузке файла из S3: " + e.getMessage(), e);
        }
    }

    @Override
    public List<File> getFilesInDirectory(Long directoryId) {
        return directoryRepository.findAllByDirectoryId(directoryId);
    }


    @Override
    public File findFileById(Long id) {
        return fileRepository.getFileById(id);
    }

    @Override
    @Transactional
    public void deleteFile(Long fileId) {
        // Находим файл в базе данных
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("Файл не найден: " + fileId));

        // Удаляем файл из S3
        String s3Key = file.getStoragePath();
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build());
            System.out.println("Файл удален из S3: " + s3Key);
        } catch (Exception e) {
            System.err.println("Ошибка при удалении файла из S3: " + s3Key + ", " + e.getMessage());
        }

        file.setActive(false);
        linkService.updateAllActiveByFile(file);
        // Удаляем файл из базы данных
        fileRepository.save(file);
    }



    @Override
    @Transactional
    public void updateActivityFilesByUser(String username) {
        linkService.updateAllActiveByUsername(username);
        List<File> files = fileRepository.findByUsername(username);
        for (File file : files) {
            file.setActive(false);
            fileRepository.save(file);
        }
    }

    @Override
    public List<File> findByUser(String username) {
        return fileRepository.findByUsername(username);
    }

    @Override
    public List<File> getFilesInRootDirectory() {
        User user =securityUtils.getCurrentUser();
        return fileRepository.findAllByUserAndDirectoryIsNull(user);
    }




    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            return fileName.substring(dotIndex + 1);
        }
        return "";
    }










}