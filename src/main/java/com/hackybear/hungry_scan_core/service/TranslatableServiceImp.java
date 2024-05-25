package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.entity.Translatable;
import com.hackybear.hungry_scan_core.repository.TranslatableRepository;
import com.hackybear.hungry_scan_core.service.interfaces.TranslatableService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TranslatableServiceImp implements TranslatableService {

    private final TranslatableRepository translatableRepository;

    public TranslatableServiceImp(TranslatableRepository translatableRepository) {
        this.translatableRepository = translatableRepository;
    }

    @Override
    public void saveAllTranslatables(Map<String, List<Translatable>> translatables) {
        List<Translatable> allTranslatables = extractAllLists(translatables);
        translatableRepository.saveAll(allTranslatables);
    }

    private List<Translatable> extractAllLists(Map<String, List<Translatable>> translatables) {
        List<Translatable> menuItemsTransl = translatables.get("menuItems");
        List<Translatable> ingredientsTransl = translatables.get("ingredients");
        List<Translatable> categoriesTransl = translatables.get("categories");
        List<Translatable> variantsTransl = translatables.get("variants");
        List<Translatable> zonesTransl = translatables.get("zones");
        List<Translatable> allergensTransl = translatables.get("allergens");
        List<Translatable> labelsTransl = translatables.get("labels");
        List<Translatable> result = new ArrayList<>();
        result.addAll(menuItemsTransl);
        result.addAll(ingredientsTransl);
        result.addAll(categoriesTransl);
        result.addAll(variantsTransl);
        result.addAll(zonesTransl);
        result.addAll(allergensTransl);
        result.addAll(labelsTransl);
        return result;
    }

    @Override
    public Map<String, List<Translatable>> findAllTranslatables() {
        Map<String, List<Translatable>> translatables = new HashMap<>();
        translatables.put("allergens", getAllFromAllergens());
        translatables.put("categories", getFromCategories());
        translatables.put("ingredients", getFromIngredients());
        translatables.put("labels", getFromLabels());
        translatables.put("menuItems", getFromMenuItems());
        translatables.put("variants", getFromVariants());
        translatables.put("zones", getFromZones());
        return translatables;
    }

    private List<Translatable> getAllFromAllergens() {
        return extractFromArrays(translatableRepository.findAllTranslationsFromAllergens());
    }

    private List<Translatable> getFromCategories() {
        return translatableRepository.findAllTranslationsFromCategories();
    }

    private List<Translatable> getFromIngredients() {
        return translatableRepository.findAllTranslationsFromIngredients();
    }

    private List<Translatable> getFromLabels() {
        return translatableRepository.findAllTranslationsFromLabels();
    }

    private List<Translatable> getFromMenuItems() {
        return extractFromArrays(translatableRepository.findAllTranslationsFromMenuItems());
    }

    private List<Translatable> getFromVariants() {
        return translatableRepository.findAllTranslationsFromVariants();
    }

    private List<Translatable> getFromZones() {
        return translatableRepository.findAllTranslationsFromZones();
    }

    private List<Translatable> extractFromArrays(List<Object[]> objects) {
        List<Translatable> result = new ArrayList<>();
        for (Object[] array : objects) {
            for (Object obj : array) {
                if (obj instanceof Translatable) {
                    result.add((Translatable) obj);
                } else {
                    throw new ClassCastException("Element is not of type Translatable");
                }
            }
        }
        return result;
    }
}
