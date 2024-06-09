package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.entity.MenuItem;
import com.hackybear.hungry_scan_core.exception.LocalizedException;

import java.util.List;

public interface MenuItemService {

    List<MenuItem> findAll();

    MenuItem findById(Integer id) throws LocalizedException;

    void save(MenuItem menuItem) throws Exception;

    void delete(Integer id) throws LocalizedException;
}
