package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.dto.VariantDTO;
import com.hackybear.hungry_scan_core.exception.LocalizedException;

import java.util.List;

public interface VariantService {

    VariantDTO findById(Long id) throws LocalizedException;

    List<VariantDTO> findAllByMenuItemId(Long menuItemId);

    void save(VariantDTO variantDTO) throws Exception;

    void update(VariantDTO variantDTO) throws Exception;

    List<VariantDTO> updateDisplayOrders(List<VariantDTO> variantDTOs);

    List<VariantDTO> delete(Long id) throws LocalizedException;
}
