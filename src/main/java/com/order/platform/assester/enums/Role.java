package com.order.platform.assester.enums;

/**
 * author: user,
 * date: 02.10.2026
 */

public enum Role {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    private String roleName;

    Role(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }
}