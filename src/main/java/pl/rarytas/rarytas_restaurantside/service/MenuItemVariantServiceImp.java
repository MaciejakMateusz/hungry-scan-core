package pl.rarytas.rarytas_restaurantside.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.rarytas.rarytas_restaurantside.entity.MenuItemVariant;
import pl.rarytas.rarytas_restaurantside.exception.ExceptionHelper;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.repository.MenuItemVariantRepository;
import pl.rarytas.rarytas_restaurantside.service.interfaces.MenuItemVariantService;

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