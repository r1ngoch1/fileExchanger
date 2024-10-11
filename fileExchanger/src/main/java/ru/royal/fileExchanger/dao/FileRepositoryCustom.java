package ru.royal.fileExchanger.dao;

import org.springframework.data.repository.query.Param;
import ru.royal.fileExchanger.entities.File;

import java.util.List;

public interface FileRepositoryCustom {
    List<File> findByFileName(String fileName);

    List<File> findByUsername(String username);

}
