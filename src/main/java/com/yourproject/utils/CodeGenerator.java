package com.yourproject.utils;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

public class CodeGenerator {
    private static final SecureRandom random = new SecureRandom();
    private static final Set<String> activeTokens = new HashSet<>();

    public static String generateUniqueCode() {
        String code;
        do {
            code = String.format("%06d", random.nextInt(1000000));
        } while (activeTokens.contains(code));
        activeTokens.add(code);
        return code;
    }

    public static void removeCode(String code) {
        activeTokens.remove(code);
    }
}
