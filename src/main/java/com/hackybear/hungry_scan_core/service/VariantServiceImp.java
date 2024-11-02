package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.dto.VariantDTO;
import com.hackybear.hungry_scan_core.dto.mapper.TranslatableMapper;
import com.hackybear.hungry_scan_core.dto.mapper.VariantMapper;
import com.hackybear.hungry_scan_core.entity.Translatable;
import com.hackybear.hungry_scan_core.entity.Variant;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.VariantRepository;
import com.hackybear.hungry_scan_core.service.interfaces.VariantService;
import com.hackybear.hungry_scan_core.utility.SortingHelper;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class VariantServiceImp implements VariantService {

    private final VariantRepository variantRepository;
    private final ExceptionHelper exceptionHelper;
    private final SortingHelper sortingHelper;
    private final VariantMapper variantMapper;
    private final TranslatableMapper translatableMapper;
    private final EntityManager entityManager;

    public VariantServiceImp(VariantRepository variantRepository,
                             ExceptionHelper exceptionHelper,
                             SortingHelper sortingHelper,
                             VariantMapper variantMapper,
                             TranslatableMapper translatableMapper,
                             EntityManager entityManager) {
        this.variantRepository = variantRepository;
        this.exceptionHelper = exceptionHelper;
        this.sortingHelper = sortingHelper;
        this.variantMapper = variantMapper;
        this.translatableMapper = translatableMapper;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void save(VariantDTO variantDTO) throws Exception {
        Variant variant = variantMapper.toVariant(variantDTO);
        if (!isFirstVariant(variant.getMenuItemId())) {
            switchDefaultVariant(variant);
        }
        sortingHelper.sortAndSave(variant, this::getById);
    }

    @Override
    @Transactional
    public void update(VariantDTO variantDTO) throws Exception {
        Variant existingVariant = getById(variantDTO.id());
        updateVariant(variantDTO, existingVariant);
        switchDefaultVariant(existingVariant);
        sortingHelper.sortAndSave(existingVariant, this::getById);
    }

    @Override
    public List<VariantDTO> findAllByMenuItemId(Long menuItemId) {
        List<Variant> variants = getByMenuItemId(menuItemId);
        return variants.stream().map(variantMapper::toDTO).toList();
    }

    @Override
    public VariantDTO findById(Long id) throws LocalizedException {
        Variant variant = getById(id);
        return variantMapper.toDTO(variant);
    }

    @Override
    public void delete(Long id) throws LocalizedException {
        Variant existingVariant = getById(id);
        variantRepository.delete(existingVariant);
        sortingHelper.removeAndAdjust(existingVariant);
    }

    private void updateVariant(VariantDTO variantDTO, Variant existingVariant) {
        Translatable name = translatableMapper.toTranslatable(variantDTO.name());
        existingVariant.setName(entityManager.merge(name));
        existingVariant.setMenuItemId(variantDTO.menuItemId());
        existingVariant.setPrice(variantDTO.price());
        existingVariant.setAvailable(variantDTO.available());
        existingVariant.setDefaultVariant(variantDTO.defaultVariant());
        existingVariant.setDisplayOrder(variantDTO.displayOrder());
    }

    private Variant getById(Long id) throws LocalizedException {
        return variantRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.variantService.variantNotFound", id));
    }

    private boolean isFirstVariant(Long menuItemId) {
        List<Variant> variants = getByMenuItemId(menuItemId);
        return variants.isEmpty();
    }

    private List<Variant> getByMenuItemId(Long id) {
        return variantRepository.findAllByMenuItemIdOrderByDisplayOrder(id);
    }

    private void switchDefaultVariant(Variant variant) {
        if (!variant.isDefaultVariant()) {
            return;
        }
        boolean isNewVariant = Objects.isNull(variant.getId());
        List<Variant> variants = getByMenuItemId(variant.getMenuItemId());

        if (isNewVariant) {
            for (Variant v : variants) {
                v.setDefaultVariant(false);
            }
            variantRepository.saveAll(variants);
        } else {
            for (Variant v : variants) {
                if (v.getId().equals(variant.getId())) {
                    continue;
                }
                v.setDefaultVariant(false);
            }
            variantRepository.saveAll(variants);
        }
    }
}