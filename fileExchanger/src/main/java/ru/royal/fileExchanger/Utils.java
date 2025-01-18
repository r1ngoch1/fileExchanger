package ru.royal.fileExchanger;

import java.net.URI;

public class Utils {

    public static String getContentType(String extension) {
        switch (extension.toLowerCase()) {
            case "pdf":
                return "application/pdf";
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "txt":
                return "text/plain";
            case "csv":
                return "text/csv";
            case "xlsx":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "zip":
                return "application/zip";
            default:
                return "application/octet-stream"; // По умолчанию для бинарных файлов
        }
    }

    public static String extractKeyFromUrl(String fileUrl) {
        try {
            URI uri = new URI(fileUrl);
            return uri.getPath().substring(1);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при извлечении ключа из URL: " + fileUrl, e);
        }
    }
}
