package ru.royal.fileExchanger.entities;

import jakarta.persistence.*;

import java.sql.Timestamp;
@Entity
@Table(name = "links")
public class Link {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private File file;
    @Column
    private String linkHash;
    @Column
    private Timestamp expirationDate;
    @Column
    private Timestamp createdAt;
    @Column
    private Boolean isActive;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getLinkHash() {
        return linkHash;
    }

    public void setLinkHash(String linkHash) {
        this.linkHash = linkHash;
    }

    public Timestamp getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Timestamp expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
