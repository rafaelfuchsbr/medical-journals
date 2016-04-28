package com.rfuchs.journal.application.dto;

import com.rfuchs.journal.domain.Topic;

public class TopicDTO {

    public String oid;
    public String title;
    public String description;

    public TopicDTO() {
    }

    public Topic toDomainObject() {
        return new Topic(this.oid, this.title, this.description);
    }

    @Override
    public String toString() {
        return this.oid+","+this.title+","+this.description;
    }

}