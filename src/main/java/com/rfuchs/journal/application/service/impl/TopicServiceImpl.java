package com.rfuchs.journal.application.service.impl;

import com.rfuchs.journal.application.helper.LogHelper;
import com.rfuchs.journal.application.repository.TopicRepository;
import com.rfuchs.journal.application.repository.UserRepository;
import com.rfuchs.journal.application.service.TopicService;
import com.rfuchs.journal.application.service.UserService;
import com.rfuchs.journal.domain.Topic;
import com.rfuchs.journal.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.List;

@Component("categoryService")
@Transactional
class TopicServiceImpl implements TopicService {

	private static final Logger LOGGER = LoggerFactory.getLogger(TopicServiceImpl.class);

	@Autowired
	private TopicRepository topicRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	public TopicServiceImpl(TopicRepository topicRepository) {
		this.topicRepository = topicRepository;
	}

	public TopicServiceImpl() {
	}

	@Override
	public Topic getByOid(String oid) {
		long start = System.currentTimeMillis();
		final String parameters = String.format("[oid=%s]", oid);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace(LogHelper.methodEntering(parameters));
		}

		Assert.notNull(oid, "OID must not be null");
		Topic topic = this.topicRepository.getByOid(oid);

		LOGGER.info(LogHelper.timedLog(parameters, start));
		return topic;
	}

	@Override
	public Topic save(Topic topic) {
		long start = System.currentTimeMillis();
		final String parameters = String.format("[topic=%s]", topic);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace(LogHelper.methodEntering(parameters));
		}

		topic = this.topicRepository.save(topic);

		LOGGER.info(LogHelper.timedLog(parameters, start));
		return topic;
	}

	@Override
	public Page<Topic> findAll(String filter, Integer page, Integer itemsPerPage, String sort) {
		long start = System.currentTimeMillis();
		final String parameters = String.format("[filter=%s],[page=%s],[itemsPerPage=%s],[sort=%s]", filter, page, itemsPerPage, sort);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace(LogHelper.methodEntering(parameters));
		}

		PageRequest pageRequest = new PageRequest(
				page == null ? 0 : page-1,
				itemsPerPage == null ? 20 : itemsPerPage,
				new Sort(
						new Sort.Order(Sort.Direction.ASC, StringUtils.isEmpty(sort) ? "title" : sort)
				)
		);

		Page<Topic> catPage = topicRepository.findByTitleContainingOrDescriptionContainingAllIgnoreCase(
				StringUtils.isEmpty(filter) ? "" : filter,
				StringUtils.isEmpty(filter) ? "" : filter,
				pageRequest);

		LOGGER.info(LogHelper.timedLog(parameters, start));
		return catPage;
	}

	@Override
	public List<Topic> findAll() {
		long start = System.currentTimeMillis();
		final String parameters = "no params";
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace(LogHelper.methodEntering(parameters));
		}

		List<Topic> categories = topicRepository.findAll();

		LOGGER.info(LogHelper.timedLog(parameters, start));
		return categories;
	}

	@Override
	public User subscribeUser(String categoryOid, String userOid) {
		long start = System.currentTimeMillis();
		final String parameters = String.format("[categoryOid=%s],[userOid=%s]", categoryOid, userOid);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace(LogHelper.methodEntering(parameters));
		}

		User user = userService.getByOid(userOid);

		user.topics.add(topicRepository.getByOid(categoryOid));

		try {
			user = userRepository.save(user);
		} catch (Exception e) {
			LOGGER.error("Error while saving user subscription - " + e.getMessage());
		}

		LOGGER.info(LogHelper.timedLog(parameters, start));
		return user;
	}

	@Override
	public User unsubscribeUser(String categoryOid, String userOid) {
		long start = System.currentTimeMillis();
		final String parameters = String.format("[categoryOid=%s],[userOid=%s]", categoryOid, userOid);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace(LogHelper.methodEntering(parameters));
		}

		User user = userService.getByOid(userOid);

		user.topics.remove(topicRepository.getByOid(categoryOid));

		try {
			user = userRepository.save(user);
		} catch (Exception e) {
			LOGGER.error("Error while saving user unsubscription - " + e.getMessage());
		}

		LOGGER.info(LogHelper.timedLog(parameters, start));
		return user;
	}
}
