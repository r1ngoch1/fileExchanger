package ru.royal.fileExchanger.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import ru.royal.fileExchanger.entities.File;
import ru.royal.fileExchanger.entities.User;
import java.util.List;

@RepositoryRestResource
public interface FileRepository extends CrudRepository<File, Long> {
    /**
     *
     * @param fileName
     * @return список найденных файлов с заданным именем
     */
    List<File> findByFileName(String fileName);

    /**
     *
     * @param username
     * @return список файлов у конкретного пользователя
     */
    @Query("SELECT f from File f join f.user u WHERE u.username = :username")
    List<File> findByUsername(@Param("username") String username);

    /**
     * удаляет файл(в том числе все ссылки на него) по имени файла
     * @param fileName
     */
    void deleteByFileName(String fileName);

    /**
     *
     * @param user
     *
     * удаляет все файлы у пользователя
     */
    void deleteAllByUser(User user);

}
