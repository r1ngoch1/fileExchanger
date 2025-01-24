package ru.royal.fileExchanger.service;


import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.royal.fileExchanger.SecurityUtils;
import ru.royal.fileExchanger.entities.Directory;
import ru.royal.fileExchanger.entities.File;
import ru.royal.fileExchanger.entities.Link;
import ru.royal.fileExchanger.entities.User;
import ru.royal.fileExchanger.repository.DirectoryRepository;
import ru.royal.fileExchanger.repository.FileRepository;
import ru.royal.fileExchanger.repository.LinkRepository;
import ru.royal.fileExchanger.repository.UserRepository;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.*;


import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class DirectoryServiceImpl implements DirectoryService {

    private final DirectoryRepository directoryRepository;
    private final SecurityUtils securityUtils;
    private final FileRepository fileRepository;
    private final S3Client s3Client;
    private final LinkRepository linkRepository;

    private final String bucketName = "fileexchanger";


    @Autowired
    public DirectoryServiceImpl(DirectoryRepository directoryRepository, SecurityUtils securityUtils, FileRepository fileRepository
    , S3Client s3Client, LinkRepository linkRepository) {
        this.directoryRepository = directoryRepository;
        this.securityUtils = securityUtils;
        this.fileRepository = fileRepository;
        this.s3Client = s3Client;
        this.linkRepository = linkRepository;
    }

    @Override
    public Directory createDirectory(String name, Long parentDirectoryId) {
        User currentUser = securityUtils.getCurrentUser();
        Directory parentDirectory = null;

        if (parentDirectoryId != null) {
            parentDirectory = directoryRepository.findById(parentDirectoryId)
                    .orElseThrow(() -> new IllegalArgumentException("Родительская директория не найдена"));
        }

        String s3Path;
        if (parentDirectoryId != 0L) {
            s3Path = parentDirectory.getS3Path() + "/" + name; // Добавляем в путь имя директории
        } else {
            assert parentDirectory != null;
            s3Path = parentDirectory.getS3Path() + name; // Для корневой директории
        }

        Directory newDirectory = new Directory();
        newDirectory.setName(name);
        newDirectory.setUser(currentUser);
        newDirectory.setParentDirectory(parentDirectory);
        newDirectory.setS3Path(s3Path);
        newDirectory.setActive(true);

        return directoryRepository.save(newDirectory);
    }

    @Override
    public List<Directory> getSubdirectories(Long directoryId) {
        return directoryRepository.findSubdirectoriesByDirectoryId(directoryId);
    }

    @Override
    public List<Directory> getDirectoriesByUser() {
        User currentUser = securityUtils.getCurrentUser();
        return directoryRepository.findDirectoriesByUser(currentUser);
    }

    @Override
    public Directory getDirectoryById(Long directoryId) {
        return directoryRepository.findById(directoryId)
                .orElseThrow(() -> new IllegalArgumentException("Директория с ID " + directoryId + " не найдена."));
    }


    @Override
    public List<Directory> getRootDirectoriesByUser() {
        User currentUser = securityUtils.getCurrentUser();
        return directoryRepository.findRootDirectories(currentUser);
    }

    @Override
    @Transactional
    public void deleteDirectory(Long directoryId){

        Directory directory = directoryRepository.findById(directoryId)
                .orElseThrow(() -> new RuntimeException("Директория не найдена" + directoryId));

        String s3Key = directory.getS3Path();
        try {
            // Удаляем все объекты с данным префиксом
            ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .prefix(s3Key.endsWith("/") ? s3Key : s3Key + "/")
                    .build();

            ListObjectsV2Response listResponse = s3Client.listObjectsV2(listRequest);

            for (S3Object s3Object : listResponse.contents()) {
                s3Client.deleteObject(DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(s3Object.key())
                        .build());
                System.out.println("Удалён объект: " + s3Object.key());
            }

            System.out.println("Директория удалена из S3: " + s3Key);
        } catch (Exception e) {
            System.err.println("Ошибка при удалении директории: " + s3Key + ", " + e.getMessage());
        }

        List<File> files = directory.getFiles();
        for (File file : files) fileRepository.delete(file);
        List<Link> links = directoryRepository.findLinksByDirectoryId(directoryId);
        for (Link link : links) linkRepository.delete(link);

        directory.setActive(false);
        directoryRepository.save(directory);

    }

    @Override
    public void downloadDirectoryAsZip(String linkHash, String outputZipPath) throws IOException {
        Directory directory = linkRepository.findDirectoryByLinkHash(linkHash)
                .orElseThrow(() -> new RuntimeException("Директория не найдена" + linkHash));
        String directoryPath = directory.getS3Path();

        String tempDir = System.getProperty("java.io.tmpdir");
        outputZipPath = tempDir + "/directory_" + directory.getName() + ".zip";

        try (FileOutputStream fos = new FileOutputStream(outputZipPath);
             ZipOutputStream zipOut = new ZipOutputStream(fos)) {

            System.out.println("Создание архива для директории: " + directoryPath);

            // Получаем список всех объектов с указанным префиксом
            ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .prefix(directoryPath.endsWith("/") ? directoryPath : directoryPath + "/")
                    .build();

            ListObjectsV2Response listResponse = s3Client.listObjectsV2(listRequest);

            System.out.println("Найдено объектов в директории: " + listResponse.contents().size());


            for (S3Object s3Object : listResponse.contents()) {
                // Находим файл по ключу (key) в базе данных
                String s3Key = s3Object.key();
                File fileEntity = fileRepository.findByStoragePath(s3Key)
                        .orElseThrow(() -> new RuntimeException("Файл с ключом не найден в базе данных: " + s3Key));

                // Получаем оригинальное имя файла
                String originalFileName = fileEntity.getFileName();

                // Скачиваем объект в ByteArrayOutputStream
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                s3Client.getObject(
                        GetObjectRequest.builder()
                                .bucket(bucketName)
                                .key(s3Key)
                                .build(),
                        ResponseTransformer.toOutputStream(outputStream)
                );

                // Добавляем файл в архив с оригинальным именем
                zipOut.putNextEntry(new ZipEntry(originalFileName));
                zipOut.write(outputStream.toByteArray());
                zipOut.closeEntry();

                System.out.println("Файл добавлен в архив: " + originalFileName);
            }

            System.out.println("Архив создан: " + outputZipPath);
        } catch (Exception e) {
            System.err.println("Ошибка при создании архива: " + e.getMessage());
            e.printStackTrace();
        }
    }


}
