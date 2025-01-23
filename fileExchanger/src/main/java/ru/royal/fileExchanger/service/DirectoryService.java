package ru.royal.fileExchanger.service;

import ru.royal.fileExchanger.entities.Directory;
import ru.royal.fileExchanger.entities.User;

import java.io.IOException;
import java.util.List;

public interface DirectoryService {

    Directory createDirectory(String name, Long parentDirectoryId);


    List<Directory> getSubdirectories(Long directoryId);

    List<Directory> getDirectoriesByUser();

    Directory getDirectoryById(Long id);

    List<Directory> getRootDirectoriesByUser();

    void deleteDirectory(Long directoryId);
    void downloadDirectoryAsZip(Long directoryId, String outputZipPath) throws IOException;


}