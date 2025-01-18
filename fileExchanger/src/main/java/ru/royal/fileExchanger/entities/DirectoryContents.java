package ru.royal.fileExchanger.entities;

import java.util.List;

public class DirectoryContents {
    private List<File> files;
    private List<Directory> subdirectories;

    public DirectoryContents(List<File> files, List<Directory> subdirectories) {
        this.files = files;
        this.subdirectories = subdirectories;
    }

    // Геттеры и сеттеры
}

