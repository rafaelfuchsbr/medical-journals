package com.rfuchs.journal.application.enumeration;

/**
 * Created by rfuchs on 13/04/2016.
 */
public enum UserTypeEnum {
    AUTHOR("author","Author"),
    READER("reader","Reader");

    private String key;
    private String name;

    private UserTypeEnum(final String key, final String name) {
        this.key = key;
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

}
