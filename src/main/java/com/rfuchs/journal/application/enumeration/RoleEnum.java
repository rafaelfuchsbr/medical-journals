package com.rfuchs.journal.application.enumeration;

/**
 * Created by rfuchs on 13/04/2016.
 */
public enum RoleEnum {
    PUBLIC("public","Public Access"),
    READER("reader","Reader",PermissionEnum.JOURNAL_DOWNLOAD),
    AUTHOR("author","Author",PermissionEnum.JOURNAL_DOWNLOAD, PermissionEnum.JOURNAL_EDIT, PermissionEnum.JOURNAL_UPLOAD);

    private String key;
    private String name;
    private PermissionEnum[] permissions;

    RoleEnum(final String key, final String name, final PermissionEnum... permissions) {
        this.key = key;
        this.name = name;
        this.permissions = permissions;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public PermissionEnum[] getPermissions() { return permissions; }

}
