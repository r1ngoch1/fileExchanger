package ru.royal.fileExchanger.service;

import ru.royal.fileExchanger.entities.File;
import ru.royal.fileExchanger.entities.Link;

public interface LinkService {

    /**
     *
     * @param username
     * удаляет все ссылки принадлежащие пользователю по username
     */
    void updateAllActiveByUsername(String username);

    void updateAllActiveByFile(File file);

    /**
     *
     * @param filename
     * удаляет все ссылки приндлежащие файлу по имени файла
     */
    void deleteAllByFilename(String filename);

    /**
     *
     * @param fileId
     * удаялет ссылку по id файла
     */
    void deleteByFileId(Long fileId);

    /**
     *
     * @param fileId
     * @param expirationInHours
     * создает ссылку для скачивания файла по id файла, а также время действительности ссылки
     * @return
     */
    Link createLink(Long fileId, int expirationInHours);

    /**
     *
     * @param hash
     * возвращает файла по хешу ссылки
     * @return
     */
    File getFileByHash(String hash);

    /**
     *
     * @param hash
     * @return
     */
    String getFileKeyByHash(String hash);

    File getFileByLinkHash(String hash);

    Link getLinkByHash(String hash);
}
