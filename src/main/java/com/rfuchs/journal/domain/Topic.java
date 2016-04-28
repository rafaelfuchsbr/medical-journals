package com.rfuchs.journal.domain;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="CATEGORY")
public class Topic {

    @Id
    @Column(nullable = false)
    public String oid;
    @Column(nullable = false)
    public String title;
    @Column(nullable = false)
    public String description;

    public Topic(String oid, String title, String description) {
        this.oid = oid;
        this.title = title;
        this.description = description;
    }

    public Topic(String title, String description) {
        this.oid=UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
    }

    public Topic() {
        this.oid=UUID.randomUUID().toString();
    }


    @Override
    public String toString() {
        return this.oid+","+this.title+","+this.description;
    }

}