package ru.royal.fileExchanger.service;

public interface FileService {
    void deleteFile(String filename);

    void deleteFilesByUser(String username);
}
