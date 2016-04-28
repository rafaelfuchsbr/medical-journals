package com.rfuchs.journal.domain;

import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name="USER")
public class User {

    @Id
    @Column(nullable = false)
    public String oid;
    @Column(nullable = false)
    public String name;
    @Column(nullable = false)
    public String email;
    @Column(nullable = false)
    public String type;
    private String password;
    @Column(nullable = false)
    @Temporal(value= TemporalType.TIMESTAMP)
    public Date createdDate;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name="USER_TOPICS",
            joinColumns=@JoinColumn(name="USER_OID"),
            inverseJoinColumns=@JoinColumn(name="TOPIC_OID")
    )
    public Set<Topic> topics;

    public User() {
        this.oid = checkOid(null);
        this.type = checkType(null);
        this.createdDate = checkCreatedDate(null);
    }

    public User(String oid, String name, String email, String type, String password, Date createdDate) {
        this.oid = checkOid(oid);
        this.name = name;
        this.email = email;
        this.type = checkType(type);
        setPassword(password);
        this.createdDate = checkCreatedDate(createdDate);
    }

    public User(String name, String email, String type, String password, Date createdDate) {
        this.oid = checkOid(null);
        this.name = name;
        this.email = email;
        this.type = checkType(type);
        setPassword(password);
        this.createdDate = checkCreatedDate(createdDate);
    }

    public void merge(User user) {

        this.name = !StringUtils.isEmpty(user.name) ? user.name : this.name;
        this.email = !StringUtils.isEmpty(user.email) ? user.email : this.email;
        this.type = !StringUtils.isEmpty(user.type) ? user.type : this.type;
        this.password = !StringUtils.isEmpty(user.password) ? user.password : this.password;
        this.createdDate = user.createdDate != null ? user.createdDate : this.createdDate;

        updateCategories(user.topics);

    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = DigestUtils.md5DigestAsHex(password.getBytes());
    }

    public void updateCategories(Set<Topic> list) {
        if (list != null) {
            if (this.topics == null) {
                this.topics = list;
            } else {
                this.topics.addAll(list);
            }
        }
    }

    private String checkOid(String oid) {
        return StringUtils.isEmpty(oid) ? UUID.randomUUID().toString() : oid ;
    }

    private Date checkCreatedDate(Date date) {
        return (date == null) ? Calendar.getInstance().getTime() : date;
    }

    private String checkType(String type) {
        return StringUtils.isEmpty(type) ? "reader" : type ;
    }

    @Override
    public String toString() {
        return this.oid+","+this.name+","+this.email+","+this.type+","+this.createdDate;
    }

}