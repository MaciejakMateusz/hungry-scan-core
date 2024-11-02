package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.dto.AllergenDTO;
import com.hackybear.hungry_scan_core.exception.LocalizedException;

import java.util.List;

public interface AllergenService {

    void save(AllergenDTO allergenDTO);

    List<AllergenDTO> findAll();

    AllergenDTO findById(Long id) throws LocalizedException;

    void delete(Long id) throws LocalizedException;
}
