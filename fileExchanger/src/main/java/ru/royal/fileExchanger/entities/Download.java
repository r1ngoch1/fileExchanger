package ru.royal.fileExchanger.entities;

import jakarta.persistence.*;

import java.sql.Time;
import java.sql.Timestamp;
@Entity
@Table(name = "downloads")
public class Download {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private Link link;
    @ManyToOne
    private User user;
    @Column
    private String downloadIp;
    @Column
    private Timestamp downloadAt;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDownloadIp() {
        return downloadIp;
    }

    public void setDownloadIp(String downloadIp) {
        this.downloadIp = downloadIp;
    }

    public Timestamp getDownloadAt() {
        return downloadAt;
    }

    public void setDownloadAt(Timestamp downloadAt) {
        this.downloadAt = downloadAt;
    }


}
