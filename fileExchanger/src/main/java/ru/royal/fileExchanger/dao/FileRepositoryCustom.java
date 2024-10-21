package ru.royal.fileExchanger.dao;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.royal.fileExchanger.entities.File;

import java.util.List;
@Repository
public interface FileRepositoryCustom {
    List<File> findByFileName(String fileName);

    List<File> findByUsername(String username);

}
