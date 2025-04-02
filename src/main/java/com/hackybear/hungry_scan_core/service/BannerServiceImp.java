package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.dto.BannerDTO;
import com.hackybear.hungry_scan_core.dto.mapper.BannerMapper;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.BannerRepository;
import com.hackybear.hungry_scan_core.service.interfaces.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BannerServiceImp implements BannerService {

    private final BannerRepository bannerRepository;
    private final BannerMapper mapper;
    private final ExceptionHelper exceptionHelper;

    @Override
    public List<BannerDTO> findAll() {
        return bannerRepository.findAll().stream().map(mapper::toDTO).toList();
    }

    @Override
    public BannerDTO findById(String id) throws LocalizedException {
        return mapper.toDTO(bannerRepository.findById(id)
                .orElseThrow(exceptionHelper
                        .supplyLocalizedMessage("error.bannerService.bannerNotFound")));
    }

}
