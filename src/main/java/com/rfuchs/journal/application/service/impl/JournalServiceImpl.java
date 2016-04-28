package com.rfuchs.journal.application.service.impl;

import com.rfuchs.journal.application.exception.OperationNotAllowedException;
import com.rfuchs.journal.application.helper.LogHelper;
import com.rfuchs.journal.application.repository.JournalRepository;
import com.rfuchs.journal.application.service.JournalService;
import com.rfuchs.journal.domain.Journal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

@Component("journalService")
@Transactional
class JournalServiceImpl implements JournalService {

	private static final Logger LOGGER = LoggerFactory.getLogger(JournalServiceImpl.class);

	@Autowired
	private JournalRepository journalRepository;

	public JournalServiceImpl(JournalRepository journalRepository) {
		this.journalRepository = journalRepository;
	}

	public JournalServiceImpl() {

	}

	@Override
	public Journal getByOid(String oid) {
		long start = System.currentTimeMillis();
		final String parameters = String.format("[oid = %s]", oid);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace(LogHelper.methodEntering(parameters));
		}

		Journal j =this.journalRepository.getByOid(oid);

		LOGGER.info(LogHelper.timedLog(parameters, start));
		return j;
	}

	@Override
	public Journal save(Journal journal) {
		long start = System.currentTimeMillis();
		final String parameters = String.format("[journal = %s]", journal);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace(LogHelper.methodEntering(parameters));
		}

		Journal j = this.journalRepository.save(journal);

		LOGGER.info(LogHelper.timedLog(parameters, start));
		return j;
	}

	@Override
	public Page<Journal> findAll(String filter, Integer page, Integer itemsPerPage, String sort) {
		long start = System.currentTimeMillis();
		final String parameters = String.format("[filter = %s], [page = %s], [itemsPerPage = %s], [sort = %s]", filter, page, itemsPerPage, sort);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace(LogHelper.methodEntering(parameters));
		}

		PageRequest pageRequest = new PageRequest(
				page == null ? 0 : page-1,
				itemsPerPage == null ? 10 : itemsPerPage,
				new Sort(
						new Sort.Order(Sort.Direction.ASC, StringUtils.isEmpty(sort) ? "title" : sort).ignoreCase()
				)
		);

		Page<Journal> retPage = journalRepository.findByTitleContainingOrDescriptionContainingAllIgnoreCase(
				StringUtils.isEmpty(filter) ? "" : filter,
				StringUtils.isEmpty(filter) ? "" : filter,
				pageRequest);

		LOGGER.info(LogHelper.timedLog(parameters, start));
		return retPage;
	}

	@Override
	public Page<Journal> findByAuthorOidEqualsAndTitleContaining(String userOid, String filter, Integer page, Integer itemsPerPage, String sort) {
		long start = System.currentTimeMillis();
		final String parameters = String.format("[userOid = %s], [filter = %s], [page = %s], [itemsPerPage = %s], [sort = %s]", userOid, filter, page, itemsPerPage, sort);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace(LogHelper.methodEntering(parameters));
		}

		PageRequest pageRequest = new PageRequest(
				page == null ? 0 : page-1,
				itemsPerPage == null ? 10 : itemsPerPage,
				new Sort(
						new Sort.Order(Sort.Direction.ASC, StringUtils.isEmpty(sort) ? "title" : sort).ignoreCase()
				)
		);

		Page<Journal> retPage = journalRepository.findByAuthorOidEqualsAndTitleContaining(
				StringUtils.isEmpty(userOid) ? "" : userOid,
				StringUtils.isEmpty(filter) ? "" : filter,
				pageRequest);

		LOGGER.info(LogHelper.timedLog(parameters, start));
		return retPage;
	}

	@Override
	public Page<Journal> findByTopicOidOrderByTitle(String categoryOid) {
		long start = System.currentTimeMillis();
		final String parameters = String.format("[categoryOid = %s]", categoryOid);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace(LogHelper.methodEntering(parameters));
		}

		PageRequest pageRequest = new PageRequest(
				0,99999999,
				new Sort(
						new Sort.Order(Sort.Direction.ASC, "title").ignoreCase()
				)
		);

		Page<Journal> page = journalRepository.findByTopicOidOrderByTitle(categoryOid, pageRequest);

		LOGGER.info(LogHelper.timedLog(parameters, start));
		return page;
	}

	public void saveUploadedFile(MultipartFile file, String oid) throws OperationNotAllowedException {
		long start = System.currentTimeMillis();
		final String parameters = String.format("[file = %s], [oid = %s]", file.getName(), oid);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace(LogHelper.methodEntering(parameters));
		}

		if (oid == null) {
			LOGGER.error("Journal OID must be provided to upload file");
			throw new OperationNotAllowedException("Journal OID must be provided to upload file");
		}
		if (!file.isEmpty()) {
			try {
				Journal journal = getByOid(oid);
				if (journal == null) {
					LOGGER.error("Journal object can't be null to upload file");
					throw new OperationNotAllowedException("Journal object can't be null to upload file");
				}

				File newFile = new File(journal.fileFullPath(false));
				if (newFile.exists()) {
					newFile.delete();
				}

				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(journal.fileFullPath(false))));
				FileCopyUtils.copy(file.getInputStream(), stream);
				stream.close();
				LOGGER.info(String.format("File for journal %s saved - %s", journal.oid, journal.fileFullPath(false)));
			}
			catch (Exception e) {
				LOGGER.error("Error while saving file, please check stacktrace");
				e.printStackTrace();
				throw new OperationNotAllowedException("Error while saving file, please check stacktrace");
			}
		}
		else {
			LOGGER.error("File uploaded is empty - it's required in order to save it");
			throw new OperationNotAllowedException("File must be provided to save it");
		}
		LOGGER.info(LogHelper.timedLog(parameters, start));
	}

	private void deleteFile(Journal journal) {
		long start = System.currentTimeMillis();
		final String parameters = String.format("[journal = %s]", journal);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace(LogHelper.methodEntering(parameters));
		}

		File newFile = null;
		try {
			newFile = new File(journal.fileFullPath(false));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		newFile.delete();

		LOGGER.info(LogHelper.timedLog(parameters, start));
	}

	@Override
	public void delete(String oid) {
		long start = System.currentTimeMillis();
		final String parameters = String.format("[oid = %s]", oid);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace(LogHelper.methodEntering(parameters));
		}

		Journal journal = getByOid(oid);

		journalRepository.delete(journal);
		this.deleteFile(journal);

		LOGGER.info(LogHelper.timedLog(parameters, start));
	}

}
