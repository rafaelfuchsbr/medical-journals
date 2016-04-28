package com.rfuchs.journal.application.adapter;

import com.rfuchs.journal.application.dto.UserDTO;
import com.rfuchs.journal.application.helper.LogHelper;
import com.rfuchs.journal.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by rfuchs on 16/04/2016.
 */
public class UserAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserAdapter.class);

    public static UserDTO toDTO(User user) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(LogHelper.methodEntering(String.format("[user=%s]", user)));
        }

        UserDTO dto = new UserDTO();
        dto.oid = user.oid;
        dto.name = user.name;
        dto.email = user.email;
        dto.type = user.type;
        dto.password = user.getPassword();
        dto.createdDate = user.createdDate;

        dto.topics = user.topics;

        return dto;
    }

    public static User toDomain(UserDTO dto) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(LogHelper.methodEntering(String.format("[dto=%s]", dto)));
        }

        User user = new User(dto.oid, dto.name, dto.email, dto.type, dto.password, dto.createdDate);
        user.topics = dto.topics;

        return user;
    }

    public static Set<UserDTO> toDtoList(Set<User> page) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(LogHelper.methodEntering(String.format("[page=%s]", page)));
        }

        Set<UserDTO> dtos = new HashSet<UserDTO>();
        for(User user : page) {
            dtos.add(UserAdapter.toDTO(user));
        }
        return dtos;
    }

    public static Set<User> pageToSet(Page<User> page) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(LogHelper.methodEntering(String.format("[page=%s]", page)));
        }

        Set<User> users = new HashSet<User>();
        for(User user : page) {
            users.add(user);
        }
        return users;
    }

}
