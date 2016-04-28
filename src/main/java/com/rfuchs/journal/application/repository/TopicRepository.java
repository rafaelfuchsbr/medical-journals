package com.rfuchs.journal.application.repository;

import com.rfuchs.journal.domain.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TopicRepository extends CrudRepository<Topic, String> {

    Topic getByOid(String oid);

    Page<Topic> findAll(Pageable pageable);

    List<Topic> findAll();

    Page<Topic> findByTitleContainingOrDescriptionContainingAllIgnoreCase(String title, String description, Pageable pageable);

}
