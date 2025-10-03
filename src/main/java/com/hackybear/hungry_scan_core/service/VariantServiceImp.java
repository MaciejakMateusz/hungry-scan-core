package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.dto.MenuItemVariantsDTO;
import com.hackybear.hungry_scan_core.dto.mapper.MenuItemMapper;
import com.hackybear.hungry_scan_core.entity.MenuItem;
import com.hackybear.hungry_scan_core.repository.VariantRepository;
import com.hackybear.hungry_scan_core.service.interfaces.VariantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VariantServiceImp implements VariantService {

    private final VariantRepository variantRepository;
    private final MenuItemMapper menuItemMapper;

    @Override
    public List<MenuItemVariantsDTO> findAllByMenuId(Long menuId) {
        List<MenuItem> menuItems = variantRepository.findAllByMenuId(menuId);
        return menuItems.stream().map(menuItemMapper::toVariantsDTO).toList();
    }
}
