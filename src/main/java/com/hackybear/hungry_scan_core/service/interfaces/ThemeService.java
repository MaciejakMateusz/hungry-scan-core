package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.entity.Theme;
import com.hackybear.hungry_scan_core.exception.LocalizedException;

import java.util.List;

public interface ThemeService {

    List<Theme> findAll();

    Theme findById(Integer id) throws LocalizedException;

    void setActive(Integer id) throws LocalizedException;

}
