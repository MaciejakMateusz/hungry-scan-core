package com.hackybear.hungry_scan_core.entity.history;

import com.hackybear.hungry_scan_core.entity.Ingredient;
import com.hackybear.hungry_scan_core.entity.MenuItem;
import com.hackybear.hungry_scan_core.entity.MenuItemVariant;
import com.hackybear.hungry_scan_core.utility.Money;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@ToString
@NoArgsConstructor
@Table(name = "history_ordered_items")
public class HistoryOrderedItem implements Serializable {

    public HistoryOrderedItem(Long id,
                              MenuItem menuItem,
                              MenuItemVariant menuItemVariant,
                              Set<Ingredient> additionalIngredients,
                              String additionalComment,
                              Integer quantity,
                              boolean paid) {
        this.id = id;
        this.menuItem = menuItem;
        this.menuItemVariant = menuItemVariant;
        this.additionalIngredients = additionalIngredients;
        this.additionalComment = additionalComment;
        this.quantity = quantity;
        this.paid = paid;
    }

    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "menu_item_id", referencedColumnName = "id")
    private MenuItem menuItem;

    @ManyToOne
    @JoinColumn(name = "menu_item_variant_id", referencedColumnName = "id")
    private MenuItemVariant menuItemVariant;

    @ManyToMany
    @JoinTable(name = "history_ordered_item_additional_ingredients",
            joinColumns = @JoinColumn(name = "history_ordered_item_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id"))
    private Set<Ingredient> additionalIngredients = new HashSet<>();

    @Length(max = 255)
    private String additionalComment;

    @Column(nullable = false)
    @Min(value = 1, message = "Ilość musi wynosić minimum 1")
    @NotNull
    private Integer quantity;

    private BigDecimal price = Money.of(0.00);

    private boolean paid;
}