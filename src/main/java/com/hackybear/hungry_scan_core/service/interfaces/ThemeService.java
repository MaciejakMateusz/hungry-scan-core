package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.entity.Theme;
import com.hackybear.hungry_scan_core.exception.LocalizedException;

import java.util.List;

public interface ThemeService {

    List<Theme> findAll();

    Theme findById(Long id) throws LocalizedException;

    void setActive(Long id) throws LocalizedException;

    Theme getActive() throws LocalizedException;
}
