package com.example.ourlibrary.user;

import lombok.Getter;

/**
 * PackageName : com.example.ourlibrary.user
 * FileName : UserRole
 * Author : dglee
 * Create : 3/3/24 12:25 AM
 * Description :
 **/

@Getter
public enum UserRole {

    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER");
    UserRole(String value) {
        this.value = value;
    }

    private String value;
}
