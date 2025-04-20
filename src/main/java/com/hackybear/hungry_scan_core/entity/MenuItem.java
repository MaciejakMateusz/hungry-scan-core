package com.hackybear.hungry_scan_core.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hackybear.hungry_scan_core.annotation.DefaultTranslationNotBlank;
import com.hackybear.hungry_scan_core.annotation.LimitTranslationsLength;
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
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Getter
@Setter
@ToString
@EntityListeners({AuditingEntityListener.class, GeneralListener.class})
@Table(name = "menu_items")
@Entity
public class MenuItem implements Comparable<MenuItem>, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Serial
    private static final long serialVersionUID = 1L;

    private String imageName;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "translatable_name_id", referencedColumnName = "id")
    @DefaultTranslationNotBlank
    @LimitTranslationsLength
    @NotNull
    private Translatable name;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "translatable_description_id", referencedColumnName = "id")
    @LimitTranslationsLength
    private Translatable description;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    @JsonIgnore
    private Category category;

    @Column(nullable = false)
    @DecimalMin(value = "1")
    @NotNull
    private BigDecimal price = Money.of(0.00);

    private BigDecimal promoPrice = Money.of(0.00);

    @ManyToMany
    @JsonIgnore
    private Set<Banner> banners = new HashSet<>();

    @ManyToMany
    @JsonIgnore
    private Set<Label> labels = new HashSet<>();

    @ManyToMany
    @JsonIgnore
    private Set<Allergen> allergens = new HashSet<>();

    @ManyToMany
    @JsonIgnore
    private Set<Ingredient> ingredients = new HashSet<>();

    @ManyToMany
    @JsonIgnore
    private Set<Ingredient> additionalIngredients = new HashSet<>();

    @OneToMany(mappedBy = "menuItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Variant> variants = new HashSet<>();

    @NotNull
    private Integer displayOrder;

    private boolean barServed;

    private boolean available = true;

    private boolean visible = true;

    private Integer counter = 0;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updated;

    @LastModifiedBy
    private String modifiedBy;

    @CreatedBy
    private String createdBy;

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

    public void removeVariant(Variant variant) {
        this.variants.remove(variant);
    }

    public int compareTo(MenuItem other) {
        return this.displayOrder.compareTo(other.displayOrder);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MenuItem that)) return false;
        if (this.id == null || that.id == null) return false;
        return this.id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return (id != null ? id.hashCode() : System.identityHashCode(this));
    }

}