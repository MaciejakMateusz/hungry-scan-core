package com.hackybear.hungry_scan_core.entity.history;

import com.hackybear.hungry_scan_core.entity.Ingredient;
import com.hackybear.hungry_scan_core.entity.MenuItemVariant;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@Entity
@ToString
@Table(name = "history_ordered_items")
public class HistoryOrderedItem implements Serializable {

    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "menu_item_id", referencedColumnName = "id")
    private MenuItemVariant menuItemVariant;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "history_ordered_item_additional_ingredients",
            joinColumns = @JoinColumn(name = "history_ordered_item_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id"))
    private Set<Ingredient> additionalIngredients;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "history_ordered_item_excluded_ingredients",
            joinColumns = @JoinColumn(name = "history_ordered_item_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id"))
    private Set<Ingredient> excludedIngredients;

    private String comment;

    @Column(nullable = false)
    @Min(value = 1, message = "Ilość musi wynosić minimum 1")
    @NotNull
    private Integer quantity;

    boolean isReadyToServe;
}