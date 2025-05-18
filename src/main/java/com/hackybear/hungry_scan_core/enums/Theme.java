package com.hackybear.hungry_scan_core.enums;

import lombok.Getter;

@Getter
public enum Theme {

    COLOR_015C64("#015C64"),
    COLOR_078480("#078480"),
    COLOR_086FB0("#086FB0"),
    COLOR_090909("#090909"),
    COLOR_0A4CB5("#0A4CB5"),
    COLOR_0B612E("#0B612E"),
    COLOR_152966("#152966"),
    COLOR_266DD7("#266DD7"),
    COLOR_27343B("#27343B"),
    COLOR_318E41("#318E41"),
    COLOR_5C259D("#5C259D"),
    COLOR_658637("#658637"),
    COLOR_7737B3("#7737B3"),
    COLOR_9B33A6("#9B33A6"),
    COLOR_AD175A("#AD175A"),
    COLOR_C41E20("#C41E20"),
    COLOR_D9326B("#D9326B"),
    COLOR_DA8414("#DA8414"),
    COLOR_DD4B10("#DD4B10"),
    COLOR_E8AF20("#E8AF20"),
    COLOR_EF3D25("#EF3D25"),
    COLOR_F7C911("#F7C911"),
    COLOR_F97300("#F97300");

    private final String hex;

    Theme(String hex) {
        this.hex = hex;
    }

}