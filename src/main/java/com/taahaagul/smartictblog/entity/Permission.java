package com.taahaagul.smartictblog.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {

    POST_READ("post:read"),
    POST_CREATE("post:create"),
    POST_UPDATE("post:update"),
    POST_DELETE("post:delete"),
    USER_READ("user:read"),
    USER_CHANGE_ROLE("user:change-role"),
    USER_CHANGE_ENABLED("user:change-enabled")
    ;

    @Getter
    private final String permission;
}
