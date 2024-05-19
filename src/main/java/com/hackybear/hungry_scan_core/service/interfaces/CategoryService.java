package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.entity.Category;
import com.hackybear.hungry_scan_core.exception.LocalizedException;

import java.util.List;

public interface CategoryService {

    List<Category> findAll();

    List<Category> findAllAvailable();

    Category findById(Integer id) throws LocalizedException;

    void save(Category category) throws Exception;

    void delete(Integer id) throws LocalizedException;
}
