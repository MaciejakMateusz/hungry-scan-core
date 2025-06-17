package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.dto.MenuColorDTO;
import com.hackybear.hungry_scan_core.exception.LocalizedException;

import java.util.List;

public interface MenuColorService {

    void save(MenuColorDTO menuColorDTO);

    List<MenuColorDTO> findAll();

    MenuColorDTO findById(Long id) throws LocalizedException;

    void delete(Long id) throws LocalizedException;

}
