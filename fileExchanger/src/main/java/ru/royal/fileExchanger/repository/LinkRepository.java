package ru.royal.fileExchanger.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import ru.royal.fileExchanger.entities.Link;

import java.util.List;

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
}
