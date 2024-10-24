package ru.royal.fileExchanger.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.royal.fileExchanger.entities.File;
import ru.royal.fileExchanger.entities.User;

import java.util.List;
@Repository
public class FileRepositoryImpl implements FileRepositoryCustom{
    private final EntityManager entityManager;
    @Autowired
    public FileRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     *
     *
     * @param fileName
     * @return список файлов найденных по названию файла
     */
    @Override
    public List<File> findByFileName(String fileName) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<File> criteriaQuery = criteriaBuilder.createQuery(File.class);

        Root<File> fileRoot = criteriaQuery.from(File.class);
        Predicate predicate = criteriaBuilder.equal(fileRoot.get("fileName"), fileName);
        criteriaQuery.select(fileRoot).where(predicate);
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    /**
     *
     * @param username
     * @return возвращает список файлов у конкретного пользователя
     */
    @Override
    public List<File> findByUsername(String username) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<File> criteriaQuery = criteriaBuilder.createQuery(File.class);

        Root<File> fileRoot = criteriaQuery.from(File.class);
        Join<File, User> user = fileRoot.join("user", JoinType.INNER);
        Predicate predicate = criteriaBuilder.equal(user.get("username"), username);
        criteriaQuery.select(fileRoot).where(predicate);
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

}
