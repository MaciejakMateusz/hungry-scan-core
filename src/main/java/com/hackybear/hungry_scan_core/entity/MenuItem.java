package com.hackybear.hungry_scan_core.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hackybear.hungry_scan_core.annotation.DefaultTranslationNotBlank;
import com.hackybear.hungry_scan_core.listener.GeneralListener;
import com.hackybear.hungry_scan_core.utility.Money;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Getter
@Setter
@ToString
@EqualsAndHashCode
@EntityListeners(GeneralListener.class)
@Table(name = "menu_items")
@Entity
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String imageName;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "translatable_name_id", referencedColumnName = "id")
    @DefaultTranslationNotBlank
    @NotNull
    private Translatable name;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "translatable_description_id", referencedColumnName = "id")
    private Translatable description;

    @ManyToOne
    @NotNull
    private Category category;

    @Column(nullable = false)
    @DecimalMin(value = "1", message = "Cena musi być większa od 1zł")
    @NotNull
    private BigDecimal price = Money.of(0.00);

    @OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    private Set<MenuItemVariant> variants = new HashSet<>();

    @OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    private Set<Label> labels = new HashSet<>();

    @OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    private Set<Allergen> allergens = new HashSet<>();

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    private Set<Ingredient> ingredients = new HashSet<>();

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    private Set<Ingredient> additionalIngredients = new HashSet<>();

    @Column(nullable = false)
    @NotNull
    private Integer displayOrder;

    private boolean isAvailable = true;

    private boolean isNew;

    private boolean isBestseller;

    private Integer counter = 0;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updated;

    public void addVariant(MenuItemVariant variant) {
        this.variants.add(variant);
    }

    public void addLabel(Label label) {
        this.labels.add(label);
    }

    public void addAlergen(Allergen allergen) {
        this.allergens.add(allergen);
    }

    public void addIngredient(Ingredient ingredient) {
        this.ingredients.add(ingredient);
    }

    public void addAdditionalIngredient(Ingredient additionalIngredient) {
        this.additionalIngredients.add(additionalIngredient);
    }

}