package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.entity.Theme;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.ThemeRepository;
import com.hackybear.hungry_scan_core.service.interfaces.ThemeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ThemeServiceImp implements ThemeService {

    private final ThemeRepository themeRepository;
    private final ExceptionHelper exceptionHelper;

    public ThemeServiceImp(ThemeRepository themeRepository, ExceptionHelper exceptionHelper) {
        this.themeRepository = themeRepository;
        this.exceptionHelper = exceptionHelper;
    }

    @Override
    public List<Theme> findAll() {
        return themeRepository.findAll();
    }

    @Override
    public Theme findById(Long id) throws LocalizedException {
        return themeRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.themeService.themeNotFound", id));
    }

    @Override
    public void setActive(Long id) {
        List<Theme> themes = findAll();
        for (Theme theme : themes) {
            theme.setActive(theme.getId().equals(id));
        }
        themeRepository.saveAll(themes);
    }

    @Override
    public Theme getActive() throws LocalizedException {
        return themeRepository.findByActive(true).orElseThrow(exceptionHelper.supplyLocalizedMessage(
                "error.themeService.noActiveFound"));
    }


}
