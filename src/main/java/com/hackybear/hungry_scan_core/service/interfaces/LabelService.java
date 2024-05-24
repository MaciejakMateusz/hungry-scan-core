package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.entity.Label;
import com.hackybear.hungry_scan_core.exception.LocalizedException;

import java.util.List;

public interface LabelService {
    void save(Label label);

    List<Label> findAll();

    Label findById(Integer id) throws LocalizedException;

    void delete(Integer id) throws LocalizedException;
}