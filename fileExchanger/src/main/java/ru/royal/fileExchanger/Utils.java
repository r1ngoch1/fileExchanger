package ru.royal.fileExchanger;

import io.swagger.v3.oas.annotations.headers.Header;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import ru.royal.fileExchanger.entities.File;

import java.io.IOException;
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

    public static HttpHeaders getHeaderForDownload(String encodedFileName) {
        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"");
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");
        return header;
    }

    public static ResponseEntity<Resource> downloadDirectory(java.io.File zipFile,Resource resource) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + zipFile.getName() + "\"")
                .contentLength(zipFile.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    public static ResponseEntity<Resource> downloadFileUtil(File file, Resource fileResource, String encodedFileName) throws IOException {
        return ResponseEntity.ok()
                .headers(getHeaderForDownload(encodedFileName))
                .contentLength(fileResource.contentLength())
                .contentType(MediaType.parseMediaType(getContentType(file.getFileType())))
                .body(fileResource);
    }
}
