package com.rfuchs.journal.application.dto;

import com.rfuchs.journal.domain.Topic;

import java.util.Date;
import java.util.Set;

/**
 * Created by rfuchs on 13/04/2016.
 */
public class UserDTO {

    public String oid;
    public String name;
    public String email;
    public String type; //author or reader
    public String password;
    public Date createdDate;

    public Set<Topic> topics;

    public UserDTO() {
    }

    public UserDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Override
    public String toString() {
        return this.oid+","+this.name+","+this.email+","+this.type+","+this.createdDate;
    }
}
