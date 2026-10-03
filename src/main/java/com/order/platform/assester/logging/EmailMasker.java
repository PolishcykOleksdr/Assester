package com.order.platform.assester.logging;

/**
 * author: user,
 * date: 03.10.2026
 */

public final class EmailMasker {

    private EmailMasker() {
    }

    public static String mask(String email) {
        if (email == null || email.isBlank()) {
            return "<empty>";
        }

        int at = email.indexOf('@');
        if (at < 1 || at == email.length() - 1) {
            return "***";
        }

        String local = email.substring(0, at);
        String domain = email.substring(at + 1);
        return maskLocalPart(local) + "@" + domain;
    }

    private static String maskLocalPart(String local) {
        if (local.length() == 1) {
            return "*";
        }
        return local.charAt(0) + "***" + local.charAt(local.length() - 1);
    }
}