package ru.royal.fileExchanger.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import ru.royal.fileExchanger.entities.Download;

@RepositoryRestResource
public interface DownloadRepository extends CrudRepository<Download, Long> {
}
