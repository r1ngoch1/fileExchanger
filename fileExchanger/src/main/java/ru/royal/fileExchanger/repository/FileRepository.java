package ru.royal.fileExchanger.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.royal.fileExchanger.entities.File;
import ru.royal.fileExchanger.entities.User;

import java.util.List;

public interface FileRepository extends CrudRepository<File, Long> {
    List<File> findByFileName(String fileName);

    @Query("SELECT f from File f join f.user u WHERE u.username = :username")
    List<File> findByUsername(@Param("username") String username);
}
