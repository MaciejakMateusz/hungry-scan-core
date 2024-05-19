package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.entity.MenuItem;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.MenuItemRepository;
import com.hackybear.hungry_scan_core.service.interfaces.MenuItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class MenuItemServiceImp implements MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final ExceptionHelper exceptionHelper;

    public MenuItemServiceImp(MenuItemRepository menuItemRepository,
                              ExceptionHelper exceptionHelper) {
        this.menuItemRepository = menuItemRepository;
        this.exceptionHelper = exceptionHelper;
    }

    @Override
    public List<MenuItem> findAll() {
        return menuItemRepository.findAll();
    }

    @Override
    public List<MenuItem> findAllByCategoryId(Integer id) {
        return menuItemRepository.findAllByCategoryIdOrderByDisplayOrder(id);
    }

    @Override
    public MenuItem findById(Integer id) throws LocalizedException {
        return menuItemRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.menuItemService.menuItemNotFound", id));
    }

    @Override
    public void save(MenuItem menuItem) throws LocalizedException {
        sortAndSave(menuItem);
    }

    private void sortAndSave(MenuItem menuItem) throws LocalizedException {
        boolean isNew = Objects.isNull(menuItem.getId());
        MenuItem currentItem = new MenuItem();
        if (!isNew) {
            currentItem = findById(menuItem.getId());
        } else {
            menuItem = menuItemRepository.save(menuItem);
        }

        Integer currentOrder = Objects.isNull(currentItem.getDisplayOrder()) ? 0 : currentItem.getDisplayOrder();
        Integer newOrder = menuItem.getDisplayOrder();
        if (currentOrder.equals(newOrder) && newOrder != 0) {
            menuItemRepository.save(menuItem);
            return;
        }
        List<MenuItem> categoryItems = menuItemRepository.findAllByCategoryIdOrderByDisplayOrder(menuItem.getCategory().getId());

        //In case of adding first MenuItem to a Category.
        if (categoryItems.size() == 1) {
            newOrder = 1;
        }

        int maxOrder = categoryItems.size();
        if (newOrder > maxOrder) {
            newOrder = maxOrder;
        } else if (newOrder < 1) {
            newOrder = 1;
        }

        if (isNew) {
            for (MenuItem mi : categoryItems) {
                if (mi.getDisplayOrder() >= newOrder) {
                    mi.setDisplayOrder(mi.getDisplayOrder() + 1);
                }
            }
        } else {
            if (newOrder > currentOrder) {
                for (MenuItem mi : categoryItems) {
                    if (mi.getDisplayOrder() > currentOrder && mi.getDisplayOrder() <= newOrder) {
                        mi.setDisplayOrder(mi.getDisplayOrder() - 1);
                    }
                }
            } else {
                for (MenuItem mi : categoryItems) {
                    if (mi.getDisplayOrder() >= newOrder && mi.getDisplayOrder() < currentOrder) {
                        mi.setDisplayOrder(mi.getDisplayOrder() + 1);
                    }
                }
            }
        }

        for (MenuItem mi : categoryItems) {
            if (mi.getId().equals(menuItem.getId())) {
                mi.setDisplayOrder(newOrder);
            }
        }

        menuItemRepository.saveAll(categoryItems);
    }

    @Override
    public void delete(Integer id) throws LocalizedException {
        MenuItem existingMenuItem = findById(id);
        menuItemRepository.delete(existingMenuItem);
    }

}