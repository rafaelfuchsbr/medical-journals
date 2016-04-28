package com.rfuchs.journal.application.dto;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Created by rfuchs on 16/04/2016.
 */
public class PageDTO {

    public List items;
    public int size;
    public int page;
    public long totalItems;
    public int totalPages;
    public boolean hasNext;
    public boolean hasPrevious;
    public boolean isFirst;
    public boolean isLast;

    public PageDTO(Page page) {
        this.items = page.getContent();
        this.size = page.getNumberOfElements();
        this.page = page.getNumber()+1;
        this.totalItems = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.hasNext = page.hasNext();
        this.hasPrevious = page.hasPrevious();
        this.isFirst = page.isFirst();
        this.isLast = page.isLast();
    }
}
