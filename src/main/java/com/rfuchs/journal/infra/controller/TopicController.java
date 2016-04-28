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

import com.rfuchs.journal.application.adapter.TopicAdapter;
import com.rfuchs.journal.application.adapter.UserAdapter;
import com.rfuchs.journal.application.dto.TopicDTO;
import com.rfuchs.journal.application.dto.PageDTO;
import com.rfuchs.journal.application.dto.UserDTO;
import com.rfuchs.journal.application.service.TopicService;
import com.rfuchs.journal.application.service.JournalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/topics")
public class TopicController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TopicController.class);

    @Autowired
    private TopicService topicService;

    @Autowired
    private JournalService journalService;

    @RequestMapping(value = "/{oid}", method = RequestMethod.GET)
    public @ResponseBody
    TopicDTO getByOid(@PathVariable final String oid) {
        LOGGER.info("Topic requested: " + oid);
        return TopicAdapter.toDTO(this.topicService.getByOid(oid));
    }

    @RequestMapping(value = "/{oid}/journals", method = RequestMethod.GET)
    public @ResponseBody
    PageDTO getJournals(@PathVariable final String oid) {
        LOGGER.info("Topic - journal list requested: " + oid);
        return new PageDTO(this.journalService.findByTopicOidOrderByTitle(oid));
    }

    @RequestMapping(value = "", method = {RequestMethod.POST, RequestMethod.PUT})
    public @ResponseBody
    TopicDTO save(@RequestBody final TopicDTO category) {
        LOGGER.info("Saving cat: " + category.oid);
        return TopicAdapter.toDTO(this.topicService.save(TopicAdapter.toDomain(category)));
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public @ResponseBody PageDTO find(@RequestParam(required = false) String filter, @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer items, @RequestParam(required = false) String sort) {
        LOGGER.info("Listing users with " + filter);
        return new PageDTO(this.topicService.findAll(filter, page, items, sort));
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public @ResponseBody List<TopicDTO> find() {
        return TopicAdapter.toDtoList(this.topicService.findAll());
    }

    @RequestMapping(value = "/{catOid}/user/{userOid}", method = RequestMethod.POST)
    public @ResponseBody UserDTO subscribe(@PathVariable String catOid, @PathVariable String userOid) {
        return UserAdapter.toDTO(this.topicService.subscribeUser(catOid, userOid));
    }

    @RequestMapping(value = "/{catOid}/user/{userOid}", method = RequestMethod.DELETE)
    public @ResponseBody UserDTO unsubscribe(@PathVariable String catOid, @PathVariable String userOid) {
        return UserAdapter.toDTO(this.topicService.unsubscribeUser(catOid, userOid));
    }

}
