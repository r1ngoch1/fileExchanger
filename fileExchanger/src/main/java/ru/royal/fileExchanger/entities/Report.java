package ru.royal.fileExchanger.entities;


import jakarta.persistence.*;

/**
 * сущность отчет
 */
@Entity
@Table(name = "reports")
public class Report {
    @Id
    @GeneratedValue
    private Long id;

    @Column
    private ReportStatus status;

    @Column(columnDefinition = "TEXT")
    private String content;

    public Report() {

    }

    public Report(Long id, ReportStatus status, String content) {
        this.id = id;
        this.status = status;
        this.content = content;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
