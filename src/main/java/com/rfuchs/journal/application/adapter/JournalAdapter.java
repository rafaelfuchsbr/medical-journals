package com.rfuchs.journal.application.adapter;

import com.rfuchs.journal.application.dto.JournalDTO;
import com.rfuchs.journal.application.helper.LogHelper;
import com.rfuchs.journal.domain.Journal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rfuchs on 16/04/2016.
 */
public class JournalAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JournalAdapter.class);

    public static JournalDTO toDTO(Journal journal) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(LogHelper.methodEntering(String.format("[journal=%s]", journal)));
        }

        JournalDTO dto = new JournalDTO();
        dto.oid = journal.oid;
        dto.title = journal.title;
        dto.description = journal.description;
        dto.topic = journal.topic;
        dto.author = journal.author;
        dto.createdDate = journal.createdDate;
        dto.createdBy = journal.createdBy;

        return dto;
    }

    public static Journal toDomain(JournalDTO dto) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(LogHelper.methodEntering(String.format("[dto=%s]", dto)));
        }

        return new Journal(dto.oid, dto.title, dto.description, dto.topic, dto.author,
                dto.createdDate, dto.createdBy);
    }

    public static List<JournalDTO> toDtoList(List<Journal> page) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(LogHelper.methodEntering(String.format("[page=%s]", page)));
        }

        List<JournalDTO> dtos = new ArrayList<JournalDTO>();
        for(Journal journal : page) {
            dtos.add(JournalAdapter.toDTO(journal));
        }
        return dtos;
    }

    public List<JournalDTO> toDTOList(List<Journal> journals) {
        List<JournalDTO> dtoList = new ArrayList<JournalDTO>();
        for (Journal journal : journals) {
            dtoList.add(JournalAdapter.toDTO(journal));
        }

        return dtoList;
    }

    public static List<Journal> pageToList(Page<Journal> page) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(LogHelper.methodEntering(String.format("[page=%s]", page)));
        }

        List<Journal> journals = new ArrayList<Journal>();
        for(Journal journal : page) {
            journals.add(journal);
        }
        return journals;
    }

}
