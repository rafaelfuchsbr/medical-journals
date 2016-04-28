/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rfuchs.journal.infra.controller;

import com.rfuchs.journal.application.adapter.UserAdapter;
import com.rfuchs.journal.application.dto.PageDTO;
import com.rfuchs.journal.application.dto.UserDTO;
import com.rfuchs.journal.application.exception.UnauthorizedUserException;
import com.rfuchs.journal.application.exception.UserValidationException;
import com.rfuchs.journal.application.helper.LogHelper;
import com.rfuchs.journal.application.service.UserService;
import com.rfuchs.journal.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/users")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/{oid}", method = RequestMethod.GET)
	public @ResponseBody UserDTO getByOid(@PathVariable final String oid) {
        long start = System.currentTimeMillis();
        final String parameters = String.format("[oid = %s]", oid);
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(LogHelper.methodEntering(parameters));
        }

        UserDTO dto = UserAdapter.toDTO(this.userService.getByOid(oid));

        LOGGER.info(LogHelper.timedLog(parameters, start));
        return dto;
	}

    @RequestMapping(value = "", method = {RequestMethod.POST, RequestMethod.PUT})
    public @ResponseBody UserDTO save(@RequestBody final UserDTO user, HttpServletResponse response) {
        long start = System.currentTimeMillis();
        final String parameters = String.format("[user = %s]", user);
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(LogHelper.methodEntering(parameters));
        }

        User tempUser = null;
        try {
            tempUser = this.userService.save(UserAdapter.toDomain(user));
        } catch (UserValidationException e) {
            try {
                response.sendError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            } catch (Exception ex) {
                LOGGER.error(LogHelper.error("Error sending response to caller"), ex);
            }
        }

        UserDTO dto = UserAdapter.toDTO(tempUser);

        LOGGER.info(LogHelper.timedLog(parameters, start));
        return dto;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public @ResponseBody PageDTO find(@RequestParam(required = false) String filter, @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer items, @RequestParam(required = false) String sort) {
        long start = System.currentTimeMillis();
        final String parameters = String.format("[filter = %s], [page = %s], [items = %s], [sort = %s]", filter, page, items, sort);
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(LogHelper.methodEntering(parameters));
        }

        PageDTO retPageDTO = new PageDTO(this.userService.findByNameContainingIgnoreCase(filter, page, items, sort));

        LOGGER.info(LogHelper.timedLog(parameters, start));
        return retPageDTO;
    }

    @RequestMapping(value = "/type/{type}", method = RequestMethod.GET)
    public @ResponseBody PageDTO findByType(@PathVariable String type, @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer items, @RequestParam(required = false) String sort) {
        long start = System.currentTimeMillis();
        final String parameters = String.format("[type = %s], [page = %s], [items = %s], [sort = %s]", type, page, items, sort);
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(LogHelper.methodEntering(parameters));
        }

        PageDTO retPageDTO = new PageDTO(this.userService.findByTypeEqualsIgnoreCase(type, page, items, sort));

        LOGGER.info(LogHelper.timedLog(parameters, start));
        return retPageDTO;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public @ResponseBody UserDTO doLogin(@RequestBody final UserDTO user, HttpServletResponse response) {
        long start = System.currentTimeMillis();
        final String parameters = String.format("[user = %s]", user);
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(LogHelper.methodEntering(parameters));
        }

        UserDTO loggedUser = null;

        try {
            loggedUser = UserAdapter.toDTO(this.userService.doLogin(UserAdapter.toDomain(user)));
        } catch (UnauthorizedUserException e) {
            try {
                LOGGER.warn("User with email [%s] not able to login - user/password don't match or not found");
                response.sendError(HttpStatus.UNAUTHORIZED.value(), "User/password don't match!");
            } catch (Exception ex) {
                LOGGER.error(LogHelper.error("Error sending response to caller"), ex);
            }
        }

        LOGGER.info(LogHelper.timedLog(parameters, start));
        return loggedUser;
    }

}
