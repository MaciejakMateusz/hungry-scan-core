package com.hackybear.hungry_scan_core.enums;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Language {
    PL("pl"),
    EN("en"),
    DE("de"),
    FR("fr"),
    ES("es"),
    UK("uk");

    private final String name;

    Language(String name) {
        this.name = name;
    }

    public static final Map<String, Language> NAME_MAP =
            Stream.of(values()).collect(Collectors.toUnmodifiableMap(
                    t -> t.name.toUpperCase(),
                    t -> t
            ));

}
