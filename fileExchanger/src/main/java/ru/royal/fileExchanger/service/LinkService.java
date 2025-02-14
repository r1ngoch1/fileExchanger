package ru.royal.fileExchanger.service;

import ru.royal.fileExchanger.entities.Directory;
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

    void updateAllActiveByDirectory(Directory directory);


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


    Link createLink(Long objectId, int expirationInHours, boolean isDirectory);

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
