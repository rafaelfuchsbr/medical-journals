package com.rfuchs.journal.domain;

import com.rfuchs.journal.Application;
import com.rfuchs.journal.application.helper.Constants;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Entity
public class Journal implements Serializable {

    @Id
    @Column(nullable = false)
    public String oid;

    @Column(nullable = false)
    public String title;

    @Lob
    @Column(columnDefinition = "CLOB NOT NULL")
    public String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="CATEGORY")
    public Topic topic;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="AUTHOR")
    public User author;

    @Column(nullable = false)
    @Temporal(value= TemporalType.TIMESTAMP)
    public Date createdDate;

    @Column(nullable = false)
    public String createdBy;

    public Journal() {
        this.oid = UUID.randomUUID().toString();
    }

    public Journal(String title, String description, Topic topic, User author,
                   Date createdDate, String createdBy) {
        super();
        loadJournal(UUID.randomUUID().toString(),title,description, topic,author,createdDate,createdBy);
    }

    public Journal(String oid, String title, String description, Topic topic, User author,
                   Date createdDate, String createdBy) {
        super();
        loadJournal(oid, title,description, topic,author,createdDate,createdBy);
    }

    public String fileFolder() {
        return Constants.JOURNAL_PDF_MAIN_FOLDER + File.separator;
    }

    public String fileName() {
        return this.oid + Constants.JOURNAL_PDF_EXTENSION;
    }

    public String fileRelativePath() {
        return fileFolder() + fileName();
    }

    public String fileFullPath(boolean checkExists) throws FileNotFoundException {
        String fullPath = Application.getApplicationRootDirectory() + fileRelativePath();
        File file = new File(fullPath);

        try {
            if (checkExists && !file.exists()) {
                throw new FileNotFoundException(String.format("PDF Journal not found at [%s]",fullPath));
            }
        } catch ( Exception e ) {
            throw new FileNotFoundException(String.format("PDF Journal not found at [%s]",fullPath));
        }
        return fullPath;
    }

    private void loadJournal(String oid, String title, String description, Topic topic, User author,
                             Date createdDate, String createdBy) {

        this.oid = checkOid(oid);
        this.title = title;
        this.description = description;
        this.topic = topic;
        this.author = author;
        this.createdDate = checkCreatedDate(createdDate);
        this.createdBy = checkCreatedBy(createdBy);
    }

    private String checkOid(String oid) {
        if (StringUtils.isEmpty(oid)) {
            return UUID.randomUUID().toString();
        } else {
            return oid;
        }
    }

    private String checkCreatedBy(String createdBy) {
        if (StringUtils.isEmpty(createdBy)) {
            return "SYSTEM-AUTO";
        } else {
            return createdBy;
        }
    }

    private Date checkCreatedDate(Date createdDate) {
        if (createdDate == null) {
            return Calendar.getInstance().getTime();
        } else {
            return createdDate;
        }
    }

    @Override
    public String toString() {
        return this.oid+","+this.title+","+this.description+","+
                this.topic +","+this.author+
                this.createdBy+","+this.createdDate;
    }
}