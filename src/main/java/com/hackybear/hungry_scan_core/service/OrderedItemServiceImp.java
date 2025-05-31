package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.entity.OrderedItem;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.OrderedItemRepository;
import com.hackybear.hungry_scan_core.service.interfaces.OrderedItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderedItemServiceImp implements OrderedItemService {

    private final OrderedItemRepository orderedItemRepository;
    private final ExceptionHelper exceptionHelper;

    @Override
    public List<OrderedItem> findAll() {
        return orderedItemRepository.findAll();
    }

    @Override
    public List<OrderedItem> findAllDrinks() {
        return orderedItemRepository.findAllDrinks();
    }

    @Override
    public OrderedItem findById(Long id) throws LocalizedException {
        return orderedItemRepository.findById(id).orElseThrow(exceptionHelper.supplyLocalizedMessage(
                "error.orderedItemService.orderedItemNotFound", id));
    }

}