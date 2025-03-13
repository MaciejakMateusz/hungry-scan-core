package com.hackybear.hungry_scan_core.dto;


import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public record CategoryCustomerDTO(long id,
                                  TranslatableDTO name,
                                  List<MenuItemFormDTO> menuItems) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public CategoryCustomerDTO(long id,
                               TranslatableDTO name,
                               List<MenuItemFormDTO> menuItems) {
        this.id = id;
        this.menuItems = List.copyOf(menuItems);
        this.name = name;
    }

    public List<MenuItemFormDTO> menuItems() {
        return List.copyOf(this.menuItems);
    }

}