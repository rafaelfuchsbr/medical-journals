package com.rfuchs.journal.application.service;

import com.rfuchs.journal.application.exception.OperationNotAllowedException;
import com.rfuchs.journal.domain.Journal;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityExistsException;

public interface JournalService {

	Journal getByOid(String oid);

	Journal save(Journal journal) throws EntityExistsException;

	Page<Journal> findAll(String filter, Integer page, Integer itemsPerPage, String sort);

	Page<Journal> findByTopicOidOrderByTitle(String categoryOid);

	Page<Journal> findByAuthorOidEqualsAndTitleContaining(String userOid, String filter, Integer page, Integer itemsPerPage, String sort);

	void saveUploadedFile(MultipartFile file, String oid) throws OperationNotAllowedException;

	void delete(String oid);
}
