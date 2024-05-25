package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.entity.Translatable;

import java.util.List;
import java.util.Map;

public interface TranslatableService {

    void saveAllTranslatables(Map<String, List<Translatable>> translatables);

    Map<String, List<Translatable>> findAllTranslatables();

}
