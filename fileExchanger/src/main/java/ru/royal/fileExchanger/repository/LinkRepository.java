package ru.royal.fileExchanger.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ru.royal.fileExchanger.entities.Link;

import java.util.List;


public interface LinkRepository extends CrudRepository<Link, Long> {

    /**
     *
     * @param fileName
     * @return список ссылок с заданным именем файла
     */
    @Query("SELECT l from Link l join l.file f WHERE f.fileName = :fileName")
    List<Link> findByFileName(String fileName);
}
