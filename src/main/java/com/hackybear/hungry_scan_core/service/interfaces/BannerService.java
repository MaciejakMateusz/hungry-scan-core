package com.hackybear.hungry_scan_core.service.interfaces;

import com.hackybear.hungry_scan_core.dto.BannerDTO;
import com.hackybear.hungry_scan_core.exception.LocalizedException;

import java.util.List;

public interface BannerService {

    List<BannerDTO> findAll();

    BannerDTO findById(String id) throws LocalizedException;

}
