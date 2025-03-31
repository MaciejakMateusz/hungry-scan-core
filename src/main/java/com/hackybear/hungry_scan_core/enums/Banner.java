package com.hackybear.hungry_scan_core.enums;

import lombok.Getter;

@Getter
public enum Banner {
    BESTSELLER("bestseller"),
    NEW("new"),
    PROMO("promo");

    private final String methodName;

    Banner(String methodName) {
        this.methodName = methodName;
    }
}
