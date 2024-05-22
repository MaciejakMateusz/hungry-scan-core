package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.entity.Translatable;
import com.hackybear.hungry_scan_core.repository.TranslatableRepository;
import com.hackybear.hungry_scan_core.service.interfaces.TranslatableService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TranslatableServiceImp implements TranslatableService {
    private final TranslatableRepository translatableRepository;

    public TranslatableServiceImp(TranslatableRepository translatableRepository) {
        this.translatableRepository = translatableRepository;
    }

    @Override
    public void saveAllNames(List<Translatable> translatables) {
        translatableRepository.saveAll(translatables);
    }

    @Override
    public List<Translatable> findAllFromCategories() {
        return translatableRepository.findAllTranslationsFromCategories();
    }

    @Override
    public List<Object[]> findAllFromMenuItems() {
        return translatableRepository.findAllTranslationsFromMenuItems();
    }
}
