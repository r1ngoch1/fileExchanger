package ru.royal.fileExchanger.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import ru.royal.fileExchanger.entities.File;
import ru.royal.fileExchanger.entities.Link;
import ru.royal.fileExchanger.entities.User;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource
public interface LinkRepository extends CrudRepository<Link, Long> {

    /**
     *
     * @param fileName
     * @return список ссылок с заданным именем файла
     */
    @Query("SELECT l from Link l join l.file f WHERE f.fileName = :fileName")
    List<Link> findByFileName(String fileName);

    /**
     *
     * @param username
     * @return возвращает список ссылок по по логину
     */
    @Query("SELECT l from Link l WHERE l.file.user.username = :username")
    List<Link> findByUsername(String username);

    /**
     *
     * @param fileId
     * возвращает все ссылки которые привязаны к файлу
     * @return список линков
     */

    @Query("SELECT l from Link l join l.file f WHERE f.id = :fileId")
    List<Link> findByFileId(Long fileId);

    /**
     *
     * @param linkHash
     * возвращает ссылку по его хэшкоду
     * @return Link
     */

    Link findLinkByLinkHash(String linkHash);

    /**
     *
     * @param linkHash
     * возвращает файл по хэшкоду ссылки
     * @return File
     */

    @Query("select l.file from Link l WHERE l.linkHash = :linkHash")
    File findFileByLinkHash(@Param("linkHash") String linkHash);

    @Query("select l.file.user from Link l where l.id = :id")
    Optional<User> findUserByLinkId(@Param("id") Long id);




}
