package com.hackybear.hungry_scan_core.dto;


import java.util.List;

public record CategoryCustomerDTO(long id,
                                  TranslatableDTO name,
                                  List<MenuItemSimpleDTO> menuItems) {

    public CategoryCustomerDTO(long id,
                               TranslatableDTO name,
                               List<MenuItemSimpleDTO> menuItems) {
        this.id = id;
        this.menuItems = List.copyOf(menuItems);
        this.name = name;
    }

    public List<MenuItemSimpleDTO> menuItems() {
        return List.copyOf(this.menuItems);
    }

}