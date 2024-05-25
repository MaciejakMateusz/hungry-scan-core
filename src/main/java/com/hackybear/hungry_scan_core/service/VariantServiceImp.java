package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.entity.Variant;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.VariantRepository;
import com.hackybear.hungry_scan_core.service.interfaces.VariantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class VariantServiceImp implements VariantService {

    private final VariantRepository variantRepository;
    private final ExceptionHelper exceptionHelper;

    public VariantServiceImp(VariantRepository variantRepository, ExceptionHelper exceptionHelper) {
        this.variantRepository = variantRepository;
        this.exceptionHelper = exceptionHelper;
    }

    @Override
    public void save(Variant variant) {
        variantRepository.save(variant);
    }

    @Override
    public List<Variant> findAll() {
        return variantRepository.findAll();
    }

    @Override
    public Variant findById(Integer id) throws LocalizedException {
        return variantRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage("error.menuItemService.menuItemNotFound", id));
    }

    @Override
    public void delete(Integer id) throws LocalizedException {
        Variant existingVariant = findById(id);
        variantRepository.delete(existingVariant);
    }
}