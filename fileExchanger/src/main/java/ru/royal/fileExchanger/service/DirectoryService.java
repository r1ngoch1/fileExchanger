package ru.royal.fileExchanger.service;

import ru.royal.fileExchanger.entities.Directory;
import ru.royal.fileExchanger.entities.User;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface DirectoryService {

    Directory createDirectory(String name, Long parentDirectoryId);


    List<Directory> getSubdirectories(Long directoryId);

    List<Directory> getDirectoriesByUser();

    Directory getDirectoryById(Long id);

    List<Directory> getRootDirectoriesByUser();

    void deleteDirectory(Long directoryId);

    void downloadDirectoryAsZip(String linkHash, String outputZipPath) throws IOException;

    Optional<Directory> getRootDirectory();

}