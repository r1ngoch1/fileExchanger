package ru.royal.fileExchanger.entities;

import jakarta.persistence.*;

import java.math.BigInteger;
import java.sql.Timestamp;
@Entity
@Table(name = "files")
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String fileName;
    @Column
    private BigInteger fileSize;
    @Column
    private String fileType;
    @Column
    private String storagePath;
    @Column
    private Timestamp uploadedAt;
    @ManyToOne
    private User user;


    public File() {

    }
    public File(String fileName, BigInteger fileSize, String fileType, String storagePath, User user) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.storagePath = storagePath;
        this.user = user;
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

    public BigInteger getFileSize() {
        return fileSize;
    }

    public void setFileSize(BigInteger fileSize) {
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
