package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.dto.MenuColorDTO;
import com.hackybear.hungry_scan_core.dto.mapper.MenuColorMapper;
import com.hackybear.hungry_scan_core.entity.MenuColor;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.MenuColorRepository;
import com.hackybear.hungry_scan_core.service.interfaces.MenuColorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuColorServiceImp implements MenuColorService {

    private final MenuColorRepository menuColorRepository;
    private final MenuColorMapper menuColorMapper;
    private final ExceptionHelper exceptionHelper;

    @Override
    public void save(MenuColorDTO menuColorDTO) {
        menuColorRepository.save(menuColorMapper.toMenuColor(menuColorDTO));
    }

    @Override
    public List<MenuColorDTO> findAll() {
        return menuColorRepository.findAll().stream().map(menuColorMapper::toDTO).toList();
    }

    @Override
    public MenuColorDTO findById(Long id) throws LocalizedException {
        MenuColor menuColor = menuColorRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.menuColorService.menuColorNotFound"));
        return menuColorMapper.toDTO(menuColor);
    }

    @Override
    public void delete(Long id) throws LocalizedException {
        menuColorRepository.deleteById(id);
    }
}
