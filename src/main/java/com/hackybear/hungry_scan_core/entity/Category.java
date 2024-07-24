package com.hackybear.hungry_scan_core.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hackybear.hungry_scan_core.annotation.DefaultTranslationNotBlank;
import com.hackybear.hungry_scan_core.annotation.LimitTranslationsLength;
import com.hackybear.hungry_scan_core.listener.GeneralListener;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@EqualsAndHashCode
@EntityListeners(GeneralListener.class)
@Table(name = "categories")
@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "translatable_name_id", referencedColumnName = "id")
    @DefaultTranslationNotBlank
    @LimitTranslationsLength
    @NotNull
    private Translatable name;

    @OneToMany(cascade = CascadeType.MERGE, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<MenuItem> menuItems = new ArrayList<>();

    private boolean isAvailable = true;

    private boolean isBarServed;

    @NotNull
    private Integer displayOrder;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updated;

    @Override
    public String toString() {
        return name.getDefaultTranslation();
    }

    public void addMenuItem(MenuItem menuItem) {
        menuItems.add(menuItem);
    }

    public void removeMenuItem(MenuItem menuItem) {
        menuItems.removeIf(item -> Objects.equals(item.getId(), menuItem.getId()));
    }

    public List<MenuItem> getMenuItems() {
        if (!menuItems.isEmpty()) {
            menuItems.sort(Comparator.comparing(MenuItem::getDisplayOrder));
        }
        return menuItems;
    }

}