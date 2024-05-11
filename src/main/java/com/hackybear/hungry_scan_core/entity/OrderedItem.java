package com.hackybear.hungry_scan_core.entity;

import com.hackybear.hungry_scan_core.listener.OrderedItemListener;
import com.hackybear.hungry_scan_core.utility.Money;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@EntityListeners(value = OrderedItemListener.class)
@Table(name = "ordered_items")
@Entity
public class OrderedItem implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "menu_item_id", referencedColumnName = "id")
    private MenuItem menuItem;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "menu_item_variant_id", referencedColumnName = "id")
    private MenuItemVariant menuItemVariant;

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(name = "ordered_item_additional_ingredients",
            joinColumns = @JoinColumn(name = "ordered_item_id"),
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