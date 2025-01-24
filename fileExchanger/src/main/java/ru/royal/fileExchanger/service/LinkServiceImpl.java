package ru.royal.fileExchanger.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.royal.fileExchanger.entities.Directory;
import ru.royal.fileExchanger.entities.File;
import ru.royal.fileExchanger.entities.Link;
import ru.royal.fileExchanger.repository.DirectoryRepository;
import ru.royal.fileExchanger.repository.FileRepository;
import ru.royal.fileExchanger.repository.LinkRepository;


import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class LinkServiceImpl implements LinkService{
    private final LinkRepository linkRepository;
    private final FileRepository fileRepository;
    private final DirectoryRepository directoryRepository;

    @Autowired
    public LinkServiceImpl(LinkRepository linkRepository,FileRepository fileRepository,DirectoryRepository directoryRepository) {
        this.linkRepository = linkRepository;
        this.fileRepository = fileRepository;
        this.directoryRepository = directoryRepository;
    }

    @Override
    public void updateAllActiveByUsername(String username) {

        List<Link> links = linkRepository.findByUsername(username);
        for(Link link : links) {
            link.setIsActive(false);
            linkRepository.save(link);
        }
    }

    @Override
    public void updateAllActiveByFile(File file) {

        List<Link> links = linkRepository.findByFileId(file.getId());
        for(Link link : links) {
            link.setIsActive(false);
            linkRepository.save(link);
        }
    }

    @Override
    public void deleteAllByFilename(String filename) {
        List<Link> links = linkRepository.findByFileName(filename);
        linkRepository.deleteAll(links);
    }

    @Override
    public void deleteByFileId(Long fileId){
        List<Link> links = linkRepository.findByFileId(fileId);
        linkRepository.deleteAll(links);
    }

    @Override
    public Link createLink(Long objectId, int expirationInHours, boolean isDirectory) {
        Timestamp expirationDate = Timestamp.from(Instant.now().plusSeconds(expirationInHours * 3600L));
        String linkHash = UUID.randomUUID().toString();

        Link link = new Link();
        link.setLinkHash(linkHash);
        link.setExpirationDate(expirationDate);
        link.setCreatedAt(Timestamp.from(Instant.now()));
        link.setIsActive(true);

        if (isDirectory) {
            // Для директории
            Directory directory = directoryRepository.findById(objectId)
                    .orElseThrow(() -> new IllegalArgumentException("Директория не найдена"));
            link.setDirectory(directory);
        } else {
            // Для файла
            File file = fileRepository.findById(objectId)
                    .orElseThrow(() -> new IllegalArgumentException("Файл не найден"));
            link.setFile(file);
        }

        return linkRepository.save(link);
    }

    @Override
    public File getFileByHash(String hash) {
        Link link = linkRepository.findLinkByLinkHash(hash);

        // Проверка на активность и срок действия
        if (!link.getIsActive() || link.getExpirationDate().before(Timestamp.from(Instant.now()))) {
            throw new IllegalStateException("Ссылка истекла или неактивна");
        }

        return link.getFile();
    }

    @Override
    public String getFileKeyByHash(String hash) {
        // Здесь ваша логика, чтобы найти объект Link по хэшу (например, через JPA)
        Link link = linkRepository.findLinkByLinkHash(hash); // Метод, который находит Link по hash

        if (link == null || !link.getIsActive()) {
            throw new RuntimeException("Ссылка недействительна или не существует");
        }

        return linkRepository.findFileByLinkHash(hash).getStoragePath(); // Пример: файл содержит поле с ключом S3
    }
    @Override
    public File getFileByLinkHash(String hash){
        return linkRepository.findFileByLinkHash(hash);
    }

    @Override
    public Link getLinkByHash(String hash) {
        return linkRepository.findLinkByLinkHash(hash);
    }


}
