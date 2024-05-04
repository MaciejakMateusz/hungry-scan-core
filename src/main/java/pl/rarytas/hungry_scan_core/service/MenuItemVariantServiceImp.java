package pl.rarytas.hungry_scan_core.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.rarytas.hungry_scan_core.entity.MenuItemVariant;
import pl.rarytas.hungry_scan_core.exception.ExceptionHelper;
import pl.rarytas.hungry_scan_core.exception.LocalizedException;
import pl.rarytas.hungry_scan_core.repository.MenuItemVariantRepository;
import pl.rarytas.hungry_scan_core.service.interfaces.MenuItemVariantService;

import java.util.List;

@Slf4j
@Service
public class MenuItemVariantServiceImp implements MenuItemVariantService {

    private final MenuItemVariantRepository variantRepository;
    private final ExceptionHelper exceptionHelper;

    public MenuItemVariantServiceImp(MenuItemVariantRepository variantRepository, ExceptionHelper exceptionHelper) {
        this.variantRepository = variantRepository;
        this.exceptionHelper = exceptionHelper;
    }

    @Override
    public void save(MenuItemVariant variant) {
        variantRepository.save(variant);
    }

    @Override
    public List<MenuItemVariant> findAll() {
        return variantRepository.findAll();
    }

    @Override
    public MenuItemVariant findById(Integer id) throws LocalizedException {
        return variantRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage("error.menuItemService.menuItemNotFound", id));
    }

    @Override
    public void delete(Integer id) throws LocalizedException {
        MenuItemVariant existingVariant = findById(id);
        variantRepository.delete(existingVariant);
    }
}