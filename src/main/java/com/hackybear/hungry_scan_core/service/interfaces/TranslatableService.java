package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.entity.Translatable;

import java.util.List;
import java.util.Map;

public interface TranslatableService {

    void saveAll(List<Translatable> translatables, Long activeMenuId, Long activeRestaurantId);

    String translate(Map<String, Object> params);

}
