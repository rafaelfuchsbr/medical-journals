package com.rfuchs.journal.application.repository;

import com.rfuchs.journal.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.EntityNotFoundException;

public interface UserRepository extends JpaRepository<User, String> {

    User getByOid(String oid);

    User getByEmail(String email) throws EntityNotFoundException;

    Page<User> findAll(Pageable pageable);

    Page<User> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<User> findByTypeEqualsIgnoreCase(String type, Pageable pageable);
}
