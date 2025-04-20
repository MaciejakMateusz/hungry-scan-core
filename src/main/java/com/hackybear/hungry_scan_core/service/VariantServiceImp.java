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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.hackybear.hungry_scan_core.utility.Fields.VARIANTS_ALL;
import static com.hackybear.hungry_scan_core.utility.Fields.VARIANT_ID;

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
    @CacheEvict(value = VARIANTS_ALL, key = "#variantDTO.menuItem().id()")
    public void save(VariantDTO variantDTO) throws Exception {
        Variant variant = variantMapper.toVariant(variantDTO);
        if (!isFirstVariant(variant.getMenuItem().getId())) {
            switchDefaultVariant(variant);
        }
        Optional<Integer> maxDisplayOrder = variantRepository.findMaxDisplayOrderByMenuItemId(variant.getMenuItem().getId());
        variant.setDisplayOrder(maxDisplayOrder.orElse(0) + 1);
        variantRepository.save(variant);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = VARIANT_ID, key = "#variantDTO.id()"),
            @CacheEvict(value = VARIANTS_ALL, key = "#variantDTO.menuItem().id()")
    })
    public void update(VariantDTO variantDTO) throws Exception {
        Variant existingVariant = getById(variantDTO.id());
        updateVariant(variantDTO, existingVariant);
        switchDefaultVariant(existingVariant);
        variantRepository.save(existingVariant);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = VARIANTS_ALL, key = "#variantDTOs.get(0) != null ? #variantDTOs.get(0).menuItem().id() : null")
    })
    public List<VariantDTO> updateDisplayOrders(List<VariantDTO> variantDTOs) {
        List<Variant> variants = variantDTOs.stream().map(variantMapper::toVariant).toList();
        for (Variant variant : variants) {
            variantRepository.updateDisplayOrders(variant.getId(), variant.getDisplayOrder());
        }
        entityManager.clear();
        Long menuItemId = Optional.ofNullable(variants.getFirst()).map(v -> v.getMenuItem().getId()).orElse(0L);
        return getVariantByMenuItemId(menuItemId).stream().map(variantMapper::toDTO).toList();
    }

    @Override
    @Cacheable(value = VARIANTS_ALL, key = "#menuItemId")
    public List<VariantDTO> findAllByMenuItemId(Long menuItemId) {
        return getAllByMenuItemId(menuItemId);
    }

    @Override
    @Cacheable(value = VARIANT_ID, key = "#id")
    public VariantDTO findById(Long id) throws LocalizedException {
        Variant variant = getById(id);
        return variantMapper.toDTO(variant);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = VARIANT_ID, key = "#variantDTO.id()"),
            @CacheEvict(value = VARIANTS_ALL, key = "#variantDTO.menuItem().id()")
    })
    public List<VariantDTO> delete(VariantDTO variantDTO) throws LocalizedException {
        Variant existingVariant = getById(variantDTO.id());
        Long menuItemId = existingVariant.getMenuItem().getId();
        removeVariant(existingVariant);
        variantRepository.delete(existingVariant);
        List<Variant> variants = getVariantByMenuItemId(menuItemId);
        sortingHelper.reassignDisplayOrders(variants, variantRepository::saveAllAndFlush);
        return getAllByMenuItemId(menuItemId);
    }

    private void updateVariant(VariantDTO variantDTO, Variant existingVariant) {
        Translatable name = translatableMapper.toTranslatable(variantDTO.name());
        existingVariant.setName(entityManager.merge(name));
        existingVariant.setMenuItem(menuItemRepository.findById(variantDTO.menuItem().id()).orElseThrow());
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
        List<Variant> variants = getVariantByMenuItemId(variant.getMenuItem().getId());

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
        MenuItem menuItem = findMenuItemById(variant.getMenuItem().getId());
        menuItem.removeVariant(variant);
        menuItemRepository.save(menuItem);
        variantRepository.deleteById(variant.getId());
    }

    private List<VariantDTO> getAllByMenuItemId(Long menuItemId) {
        List<Variant> variants = getVariantByMenuItemId(menuItemId);
        return variants.stream().map(variantMapper::toDTO).toList();
    }
}