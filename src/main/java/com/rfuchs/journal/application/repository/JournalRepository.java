package com.rfuchs.journal.application.repository;

import com.rfuchs.journal.domain.Journal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface JournalRepository extends CrudRepository<Journal, String> {

    Journal getByOid(String oid);

    Page<Journal> findAll(Pageable pageable);

    Page<Journal> findByTitleContainingOrDescriptionContainingAllIgnoreCase(
            String title, String description, Pageable pageable);

    Page<Journal> findByTopicOidOrderByTitle(String topicOid, Pageable pageable);

    Page<Journal> findByAuthorOidEqualsAndTitleContaining(String oid, String filter, Pageable pageable);

    void delete(Journal journal);

}
