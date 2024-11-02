package com.hackybear.hungry_scan_core.dto;


import java.time.LocalDateTime;
import java.util.List;

public record CategoryDTO(long id,
                          TranslatableDTO name,
                          List<MenuItemSimpleDTO> menuItems,
                          boolean available,
                          int displayOrder,
                          LocalDateTime created,
                          LocalDateTime updated,
                          String modifiedBy,
                          String createdBy) {

    public CategoryDTO(long id,
                       TranslatableDTO name,
                       List<MenuItemSimpleDTO> menuItems,
                       boolean available,
                       int displayOrder,
                       LocalDateTime created,
                       LocalDateTime updated,
                       String modifiedBy,
                       String createdBy) {
        this.id = id;
        this.menuItems = List.copyOf(menuItems);
        this.name = name;
        this.available = available;
        this.displayOrder = displayOrder;
        this.created = created;
        this.updated = updated;
        this.modifiedBy = modifiedBy;
        this.createdBy = createdBy;
    }

    public List<MenuItemSimpleDTO> menuItems() {
        return List.copyOf(this.menuItems);
    }

}