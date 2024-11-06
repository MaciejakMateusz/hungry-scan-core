package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.dto.VariantDTO;
import com.hackybear.hungry_scan_core.dto.mapper.TranslatableMapper;
import com.hackybear.hungry_scan_core.dto.mapper.VariantMapper;
import com.hackybear.hungry_scan_core.entity.MenuItem;
import com.hackybear.hungry_scan_core.entity.Translatable;
import com.hackybear.hungry_scan_core.entity.Variant;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.MenuItemRepository;
import com.hackybear.hungry_scan_core.repository.VariantRepository;
import com.hackybear.hungry_scan_core.service.interfaces.VariantService;
import com.hackybear.hungry_scan_core.utility.SortingHelper;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class VariantServiceImp implements VariantService {

    private final VariantRepository variantRepository;
    private final ExceptionHelper exceptionHelper;
    private final SortingHelper sortingHelper;
    private final VariantMapper variantMapper;
    private final TranslatableMapper translatableMapper;
    private final EntityManager entityManager;
    private final MenuItemRepository menuItemRepository;

    public VariantServiceImp(VariantRepository variantRepository,
                             ExceptionHelper exceptionHelper,
                             SortingHelper sortingHelper,
                             VariantMapper variantMapper,
                             TranslatableMapper translatableMapper,
                             EntityManager entityManager,
                             MenuItemRepository menuItemRepository) {
        this.variantRepository = variantRepository;
        this.exceptionHelper = exceptionHelper;
        this.sortingHelper = sortingHelper;
        this.variantMapper = variantMapper;
        this.translatableMapper = translatableMapper;
        this.entityManager = entityManager;
        this.menuItemRepository = menuItemRepository;
    }

    @Override
    @Transactional
    public void save(VariantDTO variantDTO) throws Exception {
        Variant variant = variantMapper.toVariant(variantDTO);
        if (!isFirstVariant(variant.getMenuItemId())) {
            switchDefaultVariant(variant);
        }
        Optional<Integer> maxDisplayOrder = variantRepository.findMaxDisplayOrderByMenuItemId(variant.getMenuItemId());
        variant.setDisplayOrder(maxDisplayOrder.orElse(0) + 1);
        variantRepository.save(variant);
    }

    @Override
    @Transactional
    public void update(VariantDTO variantDTO) throws Exception {
        Variant existingVariant = getById(variantDTO.id());
        updateVariant(variantDTO, existingVariant);
        switchDefaultVariant(existingVariant);
        variantRepository.save(existingVariant);
    }

    @Override
    public List<VariantDTO> updateDisplayOrders(List<VariantDTO> variantDTOs) {
        List<Variant> variants = variantDTOs.stream().map(variantMapper::toVariant).toList();
        for (Variant variant : variants) {
            variantRepository.updateDisplayOrders(variant.getId(), variant.getDisplayOrder());
        }
        entityManager.clear();
        Long variantId = variants.get(0).getMenuItemId();
        return getVariantByMenuItemId(variantId).stream().map(variantMapper::toDTO).toList();
    }

    @Override
    public List<VariantDTO> findAllByMenuItemId(Long menuItemId) {
        List<Variant> variants = getVariantByMenuItemId(menuItemId);
        return variants.stream().map(variantMapper::toDTO).toList();
    }

    @Override
    public VariantDTO findById(Long id) throws LocalizedException {
        Variant variant = getById(id);
        return variantMapper.toDTO(variant);
    }

    @Override
    public List<VariantDTO> delete(Long id) throws LocalizedException {
        Variant existingVariant = getById(id);
        Long menuItemId = existingVariant.getMenuItemId();
        removeVariant(existingVariant);
        variantRepository.delete(existingVariant);
        List<Variant> variants = getVariantByMenuItemId(menuItemId);
        sortingHelper.reassignDisplayOrders(variants, variantRepository::saveAllAndFlush);
        return findAllByMenuItemId(menuItemId);
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
        List<Variant> variants = getVariantByMenuItemId(menuItemId);
        return variants.isEmpty();
    }

    private List<Variant> getVariantByMenuItemId(Long menuItemId) {
        return variantRepository.findAllByMenuItemIdOrderByDisplayOrder(menuItemId);
    }

    private MenuItem findMenuItemById(Long menuItemId) throws LocalizedException {
        return menuItemRepository.findById(menuItemId)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.menuItemService.menuItemNotFound", menuItemId));
    }

    private void switchDefaultVariant(Variant variant) {
        if (!variant.isDefaultVariant()) {
            return;
        }
        boolean isNewVariant = Objects.isNull(variant.getId());
        List<Variant> variants = getVariantByMenuItemId(variant.getMenuItemId());

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

    private void removeVariant(Variant variant) throws LocalizedException {
        MenuItem menuItem = findMenuItemById(variant.getMenuItemId());
        menuItem.removeVariant(variant);
        menuItemRepository.save(menuItem);
        variantRepository.deleteById(variant.getId());
    }
}