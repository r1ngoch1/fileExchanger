package ru.royal.fileExchanger.repository;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.royal.fileExchanger.entities.Directory;
import ru.royal.fileExchanger.entities.File;
import ru.royal.fileExchanger.entities.User;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource
public interface DirectoryRepository extends CrudRepository<Directory, Long> {
    Directory findById(long id);

    @Query("select f from File f where f.directory.id = :directoryId and f.isActive=true")
    List<File> findAllByDirectoryId(Long directoryId);

    @Query("select d.subdirectories from Directory d where d.id = :directoryId")
    List<Directory> findSubdirectoriesByDirectoryId(Long directoryId);

    @Query("SELECT DISTINCT d FROM Directory d " +
            "LEFT JOIN d.files f " +
            "WHERE d.user = :user")
    List<Directory> findDirectoriesByUser(@Param("user") User user);

    @Query("SELECT DISTINCT d FROM Directory d " +
            "LEFT JOIN d.files f " +
            "WHERE d.user = :user and d.parentDirectory.id =0")
    List<Directory> findRootDirByDirectoryId(User user);

    Optional<Directory> findById(Long id);
}
