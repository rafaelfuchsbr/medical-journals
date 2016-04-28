package com.rfuchs.journal.application.enumeration;

/**
 * Created by rfuchs on 13/04/2016.
 */
public enum PermissionEnum {
    JOURNAL_UPLOAD("journal/upload"),
    JOURNAL_EDIT("journal/edit"),
    JOURNAL_DOWNLOAD("journal/download");

    private String key;

    PermissionEnum(final String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

}
