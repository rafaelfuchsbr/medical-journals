package com.rfuchs.journal.application.service.impl;

import com.rfuchs.journal.application.enumeration.PermissionEnum;
import com.rfuchs.journal.application.enumeration.RoleEnum;
import com.rfuchs.journal.application.exception.UnauthorizedUserException;
import com.rfuchs.journal.application.exception.UserValidationException;
import com.rfuchs.journal.application.helper.Constants;
import com.rfuchs.journal.application.helper.LogHelper;
import com.rfuchs.journal.application.repository.UserRepository;
import com.rfuchs.journal.application.service.UserService;
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

import javax.persistence.EntityNotFoundException;

@Component("userService")
@Transactional
class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
	private UserRepository userRepository;

	public UserServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public UserServiceImpl() {

	}

	@Override
	public User getByOid(String oid) {
		Assert.notNull(oid, "OID must not be null");
		return this.userRepository.getByOid(oid);
	}

	@Override
	public User save(User user) throws UserValidationException {
        long start = System.currentTimeMillis();
        final String parameters = String.format("[user = %s]", user);
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(LogHelper.methodEntering(parameters));
        }

        User retUser = null;

        if (StringUtils.isEmpty(user.oid)) {
            retUser = this.userRepository.getByEmail(user.email);
        } else {
            retUser = this.userRepository.getByOid(user.email);
        }

        if (retUser != null && !retUser.oid.equalsIgnoreCase(user.oid) && retUser.email.equals(user.email)) {
            LOGGER.warn(String.format("User with email [%s] already exists", user.email));
            throw new UserValidationException("Email already registered. Please recover your password or enter different e-mail.");
        }

        retUser = this.userRepository.save(user);
        LOGGER.info(LogHelper.timedLog(parameters, start));
        return retUser;
	}


    @Override
    public User doLogin(User user) throws UnauthorizedUserException {
        long start = System.currentTimeMillis();
        final String parameters = String.format("[user = %s]", user);
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(LogHelper.methodEntering(parameters));
        }

        if (user == null) {
            LOGGER.error(LogHelper.error("Input user object is null. Cannot proceed with login process."));
            throw new UnauthorizedUserException("Input user object is null. Cannot proceed with login process.");
        }

        LOGGER.info("Starting login for user " + user.email);

        User readUser = null;

        try {
            readUser = this.userRepository.getByEmail(user.email);
            if (readUser == null) {
                throw new EntityNotFoundException(String.format("User with email [%s] not found", user.email));
            }
        } catch (EntityNotFoundException e) {
            LOGGER.error(LogHelper.error(String.format("User with email [%s] not found", user.email)), e);
            throw new UnauthorizedUserException(e.getMessage());
        }

        checkPassword(readUser, user.getPassword());

        LOGGER.info(LogHelper.timedLog(parameters, start));
        return readUser;
    }

    private Boolean checkPassword(User readUser, String password) throws UnauthorizedUserException {
        if (readUser.getPassword().equals( password )) {
            LOGGER.info(String.format("Password is good for user [%s] ",readUser.email));
            return true;
        } else {
            LOGGER.info(String.format("Passwords don't match for user %s | received = [%s] | database = [%s]",readUser.email, password, readUser.getPassword()));
            throw new UnauthorizedUserException(String.format("Password for user [%s] does not match", readUser.email));
        }
    }

    @Override
    public Page<User> findByNameContainingIgnoreCase(String filter, Integer page, Integer itemsPerPage, String sort) {
        long start = System.currentTimeMillis();
        final String parameters = String.format("[filter = %s], [page = %s], [itemsPerPage = %s], [sort = %s]", filter, page, itemsPerPage, sort);
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(LogHelper.methodEntering(parameters));
        }

        PageRequest pageRequest = new PageRequest(
                page == null ? 0 : page-1,
                itemsPerPage == null ? 5 : itemsPerPage,
                new Sort(
                      new Sort.Order(Sort.Direction.ASC, StringUtils.isEmpty(sort) ? Constants.USER_DEFAULT_SORTING : sort)
                )
        );

        if (StringUtils.isEmpty(filter)) {
            return userRepository.findAll(pageRequest);
        }

        Page<User> retPage = userRepository.findByNameContainingIgnoreCase(filter, pageRequest);

        LOGGER.info(LogHelper.timedLog(parameters, start));
        return retPage;
    }

    @Override
    public Page<User> findByTypeEqualsIgnoreCase(String type, Integer page, Integer itemsPerPage, String sort) {
        long start = System.currentTimeMillis();
        final String parameters = String.format("[type = %s], [page = %s], [itemsPerPage = %s], [sort = %s]", type, page, itemsPerPage, sort);
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(LogHelper.methodEntering(parameters));
        }

        PageRequest pageRequest = new PageRequest(
                page == null ? 0 : page-1,
                itemsPerPage == null ? 5 : itemsPerPage,
                new Sort(
                        new Sort.Order(Sort.Direction.ASC, StringUtils.isEmpty(sort) ? Constants.USER_DEFAULT_SORTING : sort)
                )
        );

        if (StringUtils.isEmpty(type)) {
            return userRepository.findAll(pageRequest);
        }

        Page<User> retPage = userRepository.findByTypeEqualsIgnoreCase(type, pageRequest);

        LOGGER.info(LogHelper.timedLog(parameters, start));
        return retPage;
    }

    private RoleEnum getEnumByKey(String key) {
        if (key.equals("reader")) {
            return RoleEnum.READER;
        } else
        if (key.equals("author")) {
            return RoleEnum.AUTHOR;
        } else {
            return RoleEnum.PUBLIC;
        }
    }

    private Boolean hasPermission(String role, String permission) {
        PermissionEnum[] permissions = getEnumByKey(role).getPermissions();
        for (PermissionEnum p : permissions) {
            if (p.getKey().equals(permission)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean checkPermission(String userFromHeader, String permission) throws  UnauthorizedUserException {

        User user = userRepository.getByOid(userFromHeader);

        if (user == null || !hasPermission(user.type, permission)) {
            throw new UnauthorizedUserException(String.format("User oid [%s] don't have permission [%s]", userFromHeader, permission));
        }

        return true;
    }

}
