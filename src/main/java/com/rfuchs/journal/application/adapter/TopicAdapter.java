package com.rfuchs.journal.application.adapter;

import com.rfuchs.journal.application.dto.TopicDTO;
import com.rfuchs.journal.application.helper.LogHelper;
import com.rfuchs.journal.domain.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rfuchs on 16/04/2016.
 */
public class TopicAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TopicAdapter.class);

    public static TopicDTO toDTO(Topic topic) {
        final String parameters = String.format("[topic=%s]", topic);
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(LogHelper.methodEntering(parameters));
        }

        TopicDTO dto = new TopicDTO();
        dto.oid = topic.oid;
        dto.title = topic.title;
        dto.description = topic.description;

        return dto;
    }

    public static Topic toDomain(TopicDTO dto) {
        final String parameters = String.format("[dto=%s]", dto);
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(LogHelper.methodEntering(parameters));
        }

        return new Topic(dto.oid, dto.title, dto.description);
    }

    public static List<TopicDTO> toDtoList(List<Topic> page) {
        final String parameters = String.format("[page=%s]", page);
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(LogHelper.methodEntering(parameters));
        }

        List<TopicDTO> dtos = new ArrayList<TopicDTO>();
        for(Topic topic : page) {
            dtos.add(TopicAdapter.toDTO(topic));
        }

        return dtos;
    }

    public static List<Topic> pageToList(Page<Topic> page) {
        final String parameters = String.format("[page=%s]", page);
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(LogHelper.methodEntering(parameters));
        }

        List<Topic> topics = new ArrayList<Topic>();
        for(Topic topic : page) {
            topics.add(topic);
        }

        return topics;
    }

}
