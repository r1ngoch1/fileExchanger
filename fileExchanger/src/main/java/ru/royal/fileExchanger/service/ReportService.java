package ru.royal.fileExchanger.service;

import ru.royal.fileExchanger.entities.Report;
import ru.royal.fileExchanger.entities.ReportStatus;

import java.util.concurrent.CompletableFuture;

public interface ReportService {
    String getContent(Long id);

    Long saveReport();

    public CompletableFuture<Report> generateReportAsync(Long reportId);

    ReportStatus getReportStatus(Long reportId);
}
