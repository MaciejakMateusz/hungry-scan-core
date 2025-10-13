package com.hackybear.hungry_scan_core.utility;

import java.security.SecureRandom;

public class RandomPasswordGenerator {

    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*()-_=+[]{};:,.<>?";
    private static final String ALL_CHARS = LOWERCASE + UPPERCASE + DIGITS + SPECIAL;
    private static final int PASSWORD_LENGTH = 10;

    private static final SecureRandom random = new SecureRandom();

    public static String generatePassword() {

        StringBuilder password = new StringBuilder();
        password.append(randomChar(LOWERCASE));
        password.append(randomChar(UPPERCASE));
        password.append(randomChar(DIGITS));
        password.append(randomChar(SPECIAL));

        for (int i = 4; i < PASSWORD_LENGTH; i++) {
            password.append(randomChar(ALL_CHARS));
        }

        return shuffle(password.toString());
    }

    private static char randomChar(String chars) {
        return chars.charAt(random.nextInt(chars.length()));
    }

    private static String shuffle(String input) {
        char[] a = input.toCharArray();
        for (int i = a.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = a[i];
            a[i] = a[j];
            a[j] = temp;
        }
        return new String(a);
    }

}