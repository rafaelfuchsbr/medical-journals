package com.rfuchs.journal.application.service;

import com.rfuchs.journal.application.exception.UnauthorizedUserException;
import com.rfuchs.journal.application.exception.UserValidationException;
import com.rfuchs.journal.domain.User;
import org.springframework.data.domain.Page;

public interface UserService {

	User getByOid(String oid);

	User doLogin(User user) throws UnauthorizedUserException;

	User save(User user) throws UserValidationException;

	Page<User> findByNameContainingIgnoreCase(String filter, Integer page, Integer itemsPerPage, String sort);

	Page<User> findByTypeEqualsIgnoreCase(String type, Integer page, Integer itemsPerPage, String sort);

	Boolean checkPermission(String userFromHeader, String permission) throws UnauthorizedUserException;

}
