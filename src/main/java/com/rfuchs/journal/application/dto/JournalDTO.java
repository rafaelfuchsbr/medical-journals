package com.rfuchs.journal.application.dto;

import com.rfuchs.journal.domain.Topic;
import com.rfuchs.journal.domain.User;

import java.util.Date;

public class JournalDTO {

    public String oid;
    public String title;
    public String description;
    public Topic topic;
    public User author;
    public Date createdDate;
    public String createdBy;

    public JournalDTO() {
    }

    @Override
    public String toString() {
        return this.oid+","+this.title+","+this.description+","+
                this.topic +","+this.author+
                this.createdBy+","+this.createdDate;
    }

}