package ru.royal.fileExchanger.repository;

import org.springframework.data.repository.CrudRepository;
import ru.royal.fileExchanger.entities.Link;

public interface LinkRepository extends CrudRepository<Link, Long> {

}
