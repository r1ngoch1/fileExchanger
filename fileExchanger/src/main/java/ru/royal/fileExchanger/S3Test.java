package ru.royal.fileExchanger;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;

import java.net.URI;
import java.nio.file.Paths;

public class S3Test {

    public static void main(String[] args) {
        S3Client s3Client = S3Client.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create("YCAJEsbB0q7P8vVV-vBZzwFJe", "YCMkCEg-ze3NhODxS0Qj9aozX1d48EfsD_Cl1PJ6")
                        )
                )
                .endpointOverride(URI.create("https://storage.yandexcloud.net"))
                .build();

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket("fileexchanger")
                        .key("C:\\Users\\sholo\\Desktop\\exchangeFile\\fileExchanger\\src\\main\\java\\ru\\royal\\fileExchanger\\test-file.txt")
                        .build(),
                RequestBody.fromFile(Paths.get("C:\\Users\\sholo\\Desktop\\exchangeFile\\fileExchanger\\src\\main\\java\\ru\\royal\\fileExchanger\\test-file.txt"))
        );

        System.out.println("Файл успешно загружен!");
    }
}

