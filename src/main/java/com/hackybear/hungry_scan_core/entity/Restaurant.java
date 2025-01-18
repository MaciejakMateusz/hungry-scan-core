package com.hackybear.hungry_scan_core.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hackybear.hungry_scan_core.listener.GeneralListener;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

@Getter
@Setter
@EqualsAndHashCode
@Table(name = "restaurants")
@EntityListeners({AuditingEntityListener.class, GeneralListener.class})
@Entity
public class Restaurant implements Comparable<Restaurant>, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(updatable = false, unique = true, length = 36)
    @Length(max = 36)
    private String token;

    @Column(length = 100, nullable = false)
    @NotBlank
    @Length(max = 100)
    private String name;

    @Column(length = 300, nullable = false)
    @NotBlank
    @Length(max = 300)
    private String address;

    @Column(length = 300, nullable = false)
    @NotBlank
    @Length(max = 300)
    private String postalCode;

    @Column(length = 300, nullable = false)
    @NotBlank
    @Length(max = 300)
    private String city;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "restaurant_id")
    private Set<Menu> menus = new TreeSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "restaurant_id")
    private Set<Allergen> allergens = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "restaurant_id")
    private Set<Ingredient> ingredients = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "restaurant_id")
    private Set<Label> labels = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "restaurant_id")
    private Set<OnboardingImage> onboardingImages = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Settings settings;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Statistics statistics;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Theme theme;

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
        return name + ", " + address;
    }

    @Override
    public int compareTo(Restaurant other) {
        return this.getName().compareTo(other.getName());
    }
}