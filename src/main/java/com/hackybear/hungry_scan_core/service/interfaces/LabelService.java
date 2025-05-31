package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.dto.LabelDTO;
import com.hackybear.hungry_scan_core.exception.LocalizedException;

import java.util.List;

public interface LabelService {

    void save(LabelDTO labelDTO);

    List<LabelDTO> findAll();

    LabelDTO findById(Long id) throws LocalizedException;

    void delete(Long id) throws LocalizedException;

}
