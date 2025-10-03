package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.dto.MenuItemVariantsDTO;

import java.util.List;

public interface VariantService {

    List<MenuItemVariantsDTO> findAllByMenuId(Long menuId);

}
