package ru.royal.fileExchanger.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import ru.royal.fileExchanger.entities.File;
import ru.royal.fileExchanger.entities.Report;
import ru.royal.fileExchanger.entities.ReportStatus;
import ru.royal.fileExchanger.entities.Role;
import ru.royal.fileExchanger.repository.FileRepository;
import ru.royal.fileExchanger.repository.ReportRepository;
import ru.royal.fileExchanger.repository.UserRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Repository
public class ReportServiceImpl implements ReportService {
    private final ReportRepository reportRepository;
    private final FileRepository fileRepository;
    private final UserRepository userRepository;

    @Autowired
    public ReportServiceImpl(ReportRepository reportRepository, FileRepository fileRepository, UserRepository userRepository) {
        this.reportRepository = reportRepository;
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
    }

    @Override
    public String getContent(Long id) {
        return reportRepository.getContentById(id);
    }

    @Override
    public Long saveReport() {
        Report report = new Report();
        report.setStatus(ReportStatus.GENERATED);
        reportRepository.save(report);
        return report.getId();
    }

    @Async
    @Override
    public CompletableFuture<Report> generateReportAsync(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));

        report.setStatus(ReportStatus.GENERATED);
        reportRepository.save(report);

        long[] times = new long[3]; // массив для хранения времени выполнения

        long reportStartTime = System.currentTimeMillis();

        CompletableFuture<Integer> userCountFuture = CompletableFuture.supplyAsync(() -> {
            long startTime = System.currentTimeMillis();
            try {
                int userCount = userRepository.countByUserRole(Role.USER);
                times[0] = System.currentTimeMillis() - startTime; // сохраняем время выполнения
                return userCount;
            } catch (Exception e) {
                report.setStatus(ReportStatus.ERROR);
                reportRepository.save(report);
                throw new RuntimeException("Ошибка при подсчете пользователей", e);
            }
        });

        CompletableFuture<List<File>> fileListFuture = CompletableFuture.supplyAsync(() -> {
            long startTime = System.currentTimeMillis();
            try {

                List<File> files = (List<File>) fileRepository.findAll();
                times[1] = System.currentTimeMillis() - startTime; // сохраняем время выполнения
                return files;
            } catch (Exception e) {
                report.setStatus(ReportStatus.ERROR);
                reportRepository.save(report);
                throw new RuntimeException("Ошибка при получении списка файлов", e);
            }
        });

        CompletableFuture<Void> allOf = CompletableFuture.allOf(userCountFuture, fileListFuture);

        allOf.thenRun(() -> {
            try {
                int userCount = userCountFuture.join();
                List<File> files = fileListFuture.join();

                // Общее время выполнения отчета
                times[2] = System.currentTimeMillis() - reportStartTime;

                String reportContent = generateHtmlReport(userCount, files, times);
                report.setContent(reportContent);
                report.setStatus(ReportStatus.COMPLETED);
            } catch (Exception e) {
                report.setStatus(ReportStatus.ERROR);
            } finally {
                reportRepository.save(report);
            }
        });


        return CompletableFuture.completedFuture(report);
    }

    @Override
    public ReportStatus getReportStatus(Long reportId) {
        return reportRepository.getStatusById(reportId);
    }

    private String generateHtmlReport(int userCount, List<File> files, long[] times) {
        StringBuilder html = new StringBuilder();
        html.append("<html><body>")
                .append("<h1>Отчет</h1>")
                .append("<p>Количество пользователей: ").append(userCount).append("</p>")
                .append("<p>Список файлов:</p>")
                .append("<table border='1'>")
                .append("<tr>")
                .append("<th>ID</th>")
                .append("<th>Имя файла</th>")
                .append("<th>Размер файла</th>")
                .append("<th>Тип файла</th>")
                .append("<th>Путь хранения</th>")
                .append("<th>Время загрузки</th>")
                .append("</tr>");

        for (File file : files) {
            html.append("<tr>")
                    .append("<td>").append(file.getId()).append("</td>")
                    .append("<td>").append(file.getFileName()).append("</td>")
                    .append("<td>").append(file.getFileSize()).append("</td>")
                    .append("<td>").append(file.getFileType()).append("</td>")
                    .append("<td>").append(file.getStoragePath()).append("</td>")
                    .append("<td>").append(file.getUploadedAt()).append("</td>")
                    .append("</tr>");
        }

        html.append("</table>")
                .append("<p>Время на подсчет пользователей: ").append(times[0]).append(" ms</p>")
                .append("<p>Время на получение списка файлов: ").append(times[1]).append(" ms</p>")
                .append("<p>Общее время на формирование отчета: ").append(times[2]).append(" ms</p>")
                .append("</body></html>");

        return html.toString();
    }
}
