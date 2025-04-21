package com.hackybear.hungry_scan_core.utility;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Objects;

@Component
public class DeepCopyUtils {

    public static String constructDuplicateName(String srcName) {
        Objects.requireNonNull(srcName, "Source name must not be null");
        if (srcName.trim().isEmpty()) {
            throw new IllegalArgumentException("Source name must not be empty");
        }
        String currentSuffix = srcName.substring(srcName.length() - 3);
        if (srcName.endsWith(" - kopia") || srcName.endsWith(" - copy")) {
            return srcName + "(1)";
        } else if (currentSuffix.matches("\\(\\d+\\)")) {
            return getSubsequentCopyName(srcName, currentSuffix);
        } else {
            return getInitialCopyName(srcName);
        }
    }

    private static String getSubsequentCopyName(String srcName, String currentSuffix) {
        int duplicateNum = Integer.parseInt(currentSuffix
                .replace("(", "")
                .replace(")", ""));
        int newDuplicateNum = duplicateNum + 1;
        String newSuffix = "(" + newDuplicateNum + ")";
        String strippedName = srcName.substring(0, srcName.length() - 3);
        return strippedName + newSuffix;
    }

    private static String getInitialCopyName(String srcName) {
        Locale locale = LocaleContextHolder.getLocale();
        String suffix = "pl".equals(locale.getLanguage()) ? "kopia" : "copy";
        return srcName + " - " + suffix;
    }

}
