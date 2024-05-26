package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.entity.Variant;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.VariantRepository;
import com.hackybear.hungry_scan_core.service.interfaces.VariantService;
import com.hackybear.hungry_scan_core.utility.SortingHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class VariantServiceImp implements VariantService {

    private final VariantRepository variantRepository;
    private final ExceptionHelper exceptionHelper;
    private final SortingHelper sortingHelper;

    public VariantServiceImp(VariantRepository variantRepository, ExceptionHelper exceptionHelper, SortingHelper sortingHelper) {
        this.variantRepository = variantRepository;
        this.exceptionHelper = exceptionHelper;
        this.sortingHelper = sortingHelper;
    }

    @Override
    public void save(Variant variant) throws Exception {
        sortingHelper.sortAndSave(variant, this::findById);
    }

    @Override
    public List<Variant> findAllByMenuItemId(Integer menuItemId) {
        return variantRepository.findAllByMenuItemIdOrderByDisplayOrder(menuItemId);
    }

    @Override
    public Variant findById(Integer id) throws LocalizedException {
        return variantRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.variantService.variantNotFound", id));
    }

    @Override
    public void delete(Integer id) throws LocalizedException {
        Variant existingVariant = findById(id);
        variantRepository.delete(existingVariant);
    }
}