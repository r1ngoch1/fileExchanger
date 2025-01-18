package ru.royal.fileExchanger.entities;


import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "directories")
public class Directory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // Имя директории

    @ManyToOne
    private User user; // Владелец директории

    @ManyToOne
    private Directory parentDirectory; // Родительская директория (null для корневых)

    @OneToMany(mappedBy = "directory")
    private List<File> files; // Файлы в директории

    @OneToMany(mappedBy = "parentDirectory")
    private List<Directory> subdirectories; // Поддиректории

    @Column(nullable = false)
    private String s3Path; // Префикс директории в S3


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Directory getParentDirectory() {
        return parentDirectory;
    }

    public void setParentDirectory(Directory parentDirectory) {
        this.parentDirectory = parentDirectory;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    public List<Directory> getSubdirectories() {
        return subdirectories;
    }

    public void setSubdirectories(List<Directory> subdirectories) {
        this.subdirectories = subdirectories;
    }

    public String getS3Path() {
        return s3Path;
    }

    public void setS3Path(String s3Path) {
        this.s3Path = s3Path;
    }
}
