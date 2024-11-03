package com.hackybear.hungry_scan_core.dto;


import java.time.LocalDateTime;
import java.util.List;

public record CategoryDTO(long id,
                          TranslatableDTO name,
                          List<MenuItemSimpleDTO> menuItems,
                          boolean available,
                          Integer displayOrder,
                          LocalDateTime created,
                          LocalDateTime updated,
                          String modifiedBy,
                          String createdBy) implements Comparable<CategoryDTO> {

    public CategoryDTO(long id,
                       TranslatableDTO name,
                       List<MenuItemSimpleDTO> menuItems,
                       boolean available,
                       Integer displayOrder,
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

    @Override
    public int compareTo(CategoryDTO other) {
        return this.displayOrder.compareTo(other.displayOrder);
    }
}