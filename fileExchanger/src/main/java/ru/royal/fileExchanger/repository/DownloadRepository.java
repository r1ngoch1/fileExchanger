package ru.royal.fileExchanger.repository;

import org.springframework.data.repository.CrudRepository;
import ru.royal.fileExchanger.entities.Download;

public interface DownloadRepository extends CrudRepository<Download, Long> {
}
