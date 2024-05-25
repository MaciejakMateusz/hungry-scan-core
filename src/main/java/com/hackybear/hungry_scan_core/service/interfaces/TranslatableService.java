package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.entity.Translatable;

import java.util.List;

public interface TranslatableService {

    void saveAllNames(List<Translatable> translatables);

    List<Translatable> findAllFromCategories();

    List<Object[]> findAllFromMenuItems();
}
