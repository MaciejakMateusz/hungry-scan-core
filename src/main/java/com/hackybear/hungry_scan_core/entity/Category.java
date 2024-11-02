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
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@EqualsAndHashCode
@EntityListeners({AuditingEntityListener.class, GeneralListener.class})
@Table(name = "categories")
@Entity
public class Category implements Comparable<Category> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long menuId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "translatable_name_id", referencedColumnName = "id")
    @DefaultTranslationNotBlank
    @LimitTranslationsLength
    @NotNull
    private Translatable name;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<MenuItem> menuItems = new ArrayList<>();

    private boolean available = true;

    private boolean barServed;

    @NotNull
    private Integer displayOrder;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updated;

    @LastModifiedBy
    private String modifiedBy;

    @CreatedBy
    private String createdBy;

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

    @Override
    public int compareTo(Category other) {
        return this.displayOrder.compareTo(other.displayOrder);
    }

}