package com.rfuchs.journal.application.service;

import com.rfuchs.journal.domain.Topic;
import com.rfuchs.journal.domain.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TopicService {

	Topic getByOid(String oid);

	Topic save(Topic topic);

	Page<Topic> findAll(String filter, Integer page, Integer itemsPerPage, String sort);

	List<Topic> findAll();

	User subscribeUser(String categoryOid, String userOid);

	User unsubscribeUser(String categoryOid, String userOid);

}
