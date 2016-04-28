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

import com.rfuchs.journal.application.adapter.JournalAdapter;
import com.rfuchs.journal.application.dto.JournalDTO;
import com.rfuchs.journal.application.dto.PageDTO;
import com.rfuchs.journal.application.exception.OperationNotAllowedException;
import com.rfuchs.journal.application.exception.UnauthorizedUserException;
import com.rfuchs.journal.application.helper.LogHelper;
import com.rfuchs.journal.application.helper.UserPermissionHelper;
import com.rfuchs.journal.application.service.JournalService;
import com.rfuchs.journal.application.service.UserService;
import com.rfuchs.journal.domain.Journal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Controller
@RequestMapping("/journals")
public class JournalController {

    private static final Logger LOGGER = LoggerFactory.getLogger(JournalController.class);

    @Autowired
    private JournalService journalService;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/{oid}", method = RequestMethod.GET)
    public @ResponseBody
    JournalDTO getByOid(@PathVariable final String oid) {
        long start = System.currentTimeMillis();
        final String parameters = String.format("[oid = %s]", oid);
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(LogHelper.methodEntering(parameters));
        }

        JournalDTO dto = JournalAdapter.toDTO(this.journalService.getByOid(oid));

        LOGGER.info(LogHelper.timedLog(parameters, start));
        return dto;
    }

    @RequestMapping(value = "/{oid}/download", method = RequestMethod.GET, produces = "application/pdf")
    public @ResponseBody ResponseEntity<InputStreamResource> downloadJournal(@PathVariable final String oid, HttpServletRequest request, HttpServletResponse response) {
        long start = System.currentTimeMillis();
        final String parameters = String.format("[oid = %s]", oid);
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(LogHelper.methodEntering(parameters));
        }

        Journal journal = this.journalService.getByOid(oid);
        ResponseEntity entity = null;
        String pdfFilePath = null;
        File pdfFile = null;

        try {
            pdfFilePath = journal.fileFullPath(true);
            pdfFile = new File(pdfFilePath);

            entity = ResponseEntity.ok().header("content-disposition", "attachment; filename=download.pdf")
                    .contentLength(pdfFile.length()).contentType(MediaType.parseMediaType("application/octet-stream"))
                    .body(new InputStreamResource(new FileInputStream(pdfFile)));

        } catch (FileNotFoundException efnf) {
            LOGGER.error(efnf.getMessage());
        } catch (Exception e) {
            LOGGER.error(String.format("Error while reading file from disk [%s]",pdfFilePath));
        }

        LOGGER.info(LogHelper.timedLog(parameters, start));
        return entity;
    }

    @RequestMapping(value = "", method = {RequestMethod.POST})
    public @ResponseBody JournalDTO save(@RequestBody final JournalDTO journal, HttpServletRequest request, HttpServletResponse response) {
        long start = System.currentTimeMillis();
        final String parameters = String.format("[journalDTO = %s]", journal);
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(LogHelper.methodEntering(parameters));
        }

        try {
            UserPermissionHelper.checkPermissionFromCookie("journal/edit", request, userService);
        } catch (UnauthorizedUserException e) {
            try {
                LOGGER.warn(e.getMessage());
                response.sendError(HttpStatus.UNAUTHORIZED.value(), "User don't have access to this service.");
            } catch (Exception ex) {
                LOGGER.error(LogHelper.error("Error sending response to caller"), ex);
            }
        }

        JournalDTO dto = JournalAdapter.toDTO(this.journalService.save(JournalAdapter.toDomain(journal)));

        LOGGER.info(LogHelper.timedLog(parameters, start));
        return dto;
    }

    @RequestMapping(value = "/{oid}", method = {RequestMethod.DELETE})
    public void delete(@PathVariable final String oid, HttpServletRequest request, HttpServletResponse response) {
        long start = System.currentTimeMillis();
        final String parameters = String.format("[oid = %s]", oid);
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(LogHelper.methodEntering(parameters));
        }

        try {
            UserPermissionHelper.checkPermissionFromCookie("journal/edit", request, userService);
        } catch (UnauthorizedUserException e) {
            try {
                LOGGER.warn(e.getMessage());
                response.sendError(HttpStatus.UNAUTHORIZED.value(), "User don't have access to this service.");
            } catch (Exception ex) {
                LOGGER.error(LogHelper.error("Error sending response to caller"), ex);
            }
        }

        this.journalService.delete(oid);

        LOGGER.info(LogHelper.timedLog(parameters, start));
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public @ResponseBody PageDTO find(
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer items,
            @RequestParam(required = false) String sort) {
        long start = System.currentTimeMillis();
        final String parameters = String.format("[filter = %s],[page = %s],[items = %s],[sort = %s]",filter,page,items,sort);
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(LogHelper.methodEntering(parameters));
        }

        PageDTO pageDTO = new PageDTO(this.journalService.findAll(filter, page, items, sort));

        LOGGER.info(LogHelper.timedLog(parameters, start));
        return pageDTO;
    }

    @RequestMapping(value = "/author/{oid}", method = RequestMethod.GET)
    public @ResponseBody PageDTO findByAuthor(
            @PathVariable String oid,
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer items,
            @RequestParam(required = false) String sort) {
        long start = System.currentTimeMillis();
        final String parameters = String.format("[oid = %s],[filter = %s],[page = %s],[items = %s],[sort = %s]",oid,filter,page,items,sort);
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(LogHelper.methodEntering(parameters));
        }

        PageDTO pageDTO = new PageDTO(this.journalService.findByAuthorOidEqualsAndTitleContaining(oid, filter, page, items, sort));

        LOGGER.info(LogHelper.timedLog(parameters, start));
        return pageDTO;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/upload/{oid}")
    public @ResponseBody void handleFileUpload(@RequestParam("file") MultipartFile file, @PathVariable String oid, HttpServletRequest request, HttpServletResponse response) {
        long start = System.currentTimeMillis();
        final String parameters = String.format("[oid = %s],[file = %s],",oid,file.getName());
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(LogHelper.methodEntering(parameters));
        }

        try {
            UserPermissionHelper.checkPermissionFromCookie("journal/upload", request, userService);
        } catch (UnauthorizedUserException e) {
            try {
                LOGGER.warn(e.getMessage());
                response.sendError(HttpStatus.UNAUTHORIZED.value(), "User don't have access to this service.");
            } catch (Exception ex) {
                LOGGER.error(LogHelper.error("Error sending response to caller"), ex);
            }
        }

        if (oid != null || file != null) {
            try {
                journalService.saveUploadedFile(file, oid);
            } catch (OperationNotAllowedException e) {
                LOGGER.error(e.getMessage());
            }
        }

        LOGGER.info(LogHelper.timedLog(parameters, start));
    }
}
