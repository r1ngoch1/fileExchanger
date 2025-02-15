package ru.royal.fileExchanger.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.royal.fileExchanger.entities.Report;
import ru.royal.fileExchanger.entities.ReportStatus;

@RepositoryRestResource
public interface ReportRepository extends CrudRepository<Report, Long> {


    /**
     * @param id возвращает контент по id отчета
     * @return content
     */
    @Query("select r.content from Report r WHERE r.id =:id")
    String getContentById(Long id);

    /**
     * @param id возвращает статут отчета по его id
     * @return status
     */
    @Query("select r.status from Report r where r.id=:id")
    ReportStatus getStatusById(Long id);
}
