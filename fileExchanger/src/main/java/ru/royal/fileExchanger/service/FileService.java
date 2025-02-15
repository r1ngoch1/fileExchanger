package ru.royal.fileExchanger.service;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import ru.royal.fileExchanger.entities.File;
import ru.royal.fileExchanger.entities.User;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface FileService {
    /**
     * @param fileId удаляет файла по его id
     */
    void deleteFile(Long fileId);

    /**
     * удаляет все файлы по имени пользователя
     *
     * @param username
     */
    void updateActivityFilesByUser(String username);

    /**
     * @param username возвращает список всех файлов у пользователя
     * @return список файлов
     */
    List<File> findByUser(String username);

    /**
     * @param file производит загрузку файла
     * @return файл как сущность
     * @throws IOException
     */
    File uploadFile(MultipartFile file, Long directoryId) throws IOException;

    /**
     * @param id возвращает файл по его id
     * @return
     */
    File findFileById(Long id);

    /**
     * @param fileName возвращает ссылку по которой файл хранится в s3 хранилище, по имени файла
     * @return url s3 storage
     */
    String generateFileUrl(String fileName);


    /**
     * @param fileKey прозводит скачивание файла по ключу файла
     * @return
     */
    Resource downloadFile(String fileKey);

    List<File> getFilesInDirectory(Long directoryId);

    List<File> getFilesInRootDirectory();

}
