package com.taahaagul.smartictblog.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.taahaagul.smartictblog.entity.Permission.*;


@RequiredArgsConstructor
public enum Role {

    USER(
            Set.of(
                    POST_READ,
                    POST_CREATE
            )

    ),

    ADMIN(
            Set.of(

                    POST_READ,
                    POST_CREATE,
                    POST_UPDATE,
                    POST_DELETE,
                    USER_READ,
                    USER_CHANGE_ROLE,
                    USER_CHANGE_ENABLED
            )
    )
    ;

    @Getter
    private final Set<Permission> permissions;

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}
