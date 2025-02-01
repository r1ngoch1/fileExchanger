package ru.royal.fileExchanger.entities;

import jakarta.persistence.*;

import java.sql.Timestamp;

/**
 *  класс представляющий сущность файл, который имеет путь до файла, название файла, размер файла
 */

@Entity
@Table(name = "files")
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String fileName;
    @Column
    private Long fileSize;
    @Column
    private String fileType;
    @Column
    private String storagePath;
    @Column
    private Timestamp uploadedAt;
    @Column
    private boolean isActive;
    @ManyToOne
    private User user;
    @ManyToOne
    private Directory directory;


    public File() {

    }

    public File(String fileName, User user) {
        this.fileName = fileName;
        this.user = user;
    }

    public File(String fileName, Long fileSize, String fileType, String storagePath, User user) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.storagePath = storagePath;
        this.user = user;
    }

    public File(String fileName) {
        this.fileName = fileName;
    }

    public Directory getDirectory() {
        return directory;
    }

    public void setDirectory(Directory directory) {
        this.directory = directory;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public Timestamp getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(Timestamp uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    @Override
    public String toString() {
        return "File{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", fileSize=" + fileSize +
                ", fileType='" + fileType + '\'' +
                ", storagePath='" + storagePath + '\'' +
                ", uploadedAt=" + uploadedAt +
                ", user=" + user +
                '}';
    }


}
