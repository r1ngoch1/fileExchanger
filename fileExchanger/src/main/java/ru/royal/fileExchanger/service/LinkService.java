package ru.royal.fileExchanger.service;

import ru.royal.fileExchanger.entities.File;

public interface LinkService {

    void deleteAllByUsername(String username);

    void deleteAllByFilename(String filename);
}
