package ru.royal.fileExchanger.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.royal.fileExchanger.entities.Report;
import ru.royal.fileExchanger.entities.ReportStatus;
import ru.royal.fileExchanger.entities.Role;
import ru.royal.fileExchanger.service.ReportService;


@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/{reportId}")
    public ResponseEntity<String> getReportFile(@PathVariable Long reportId) {
        String content = reportService.getContent(reportId);
        ReportStatus status = reportService.getReportStatus(reportId);
        switch (status){
            case ERROR -> {
                return ResponseEntity.ok("Ошибка при формировании отчета");
            }
            case GENERATED -> {
                return ResponseEntity.ok("Отчет формируется");
            }
            case COMPLETED -> {
                return ResponseEntity.ok(content);
            }
            default -> {
                return ResponseEntity.ok("-");
            }
        }

    }

    @GetMapping("/create")
    public Long createReport(){
        Long reportId = reportService.saveReport();
        reportService.generateReportAsync(reportId);
        return reportId;
    }

}
