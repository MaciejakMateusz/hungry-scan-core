package com.hackybear.hungry_scan_core.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hackybear.hungry_scan_core.annotation.AtLeastOneDayOpen;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "restaurants")
@EntityListeners({AuditingEntityListener.class})
@Entity
public class Restaurant implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(updatable = false, unique = true, length = 36)
    @Length(max = 36)
    private String token;

    private Integer qrVersion = 1;

    @NotNull
    @Column(nullable = false)
    private Long organizationId;

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

    @JsonIgnore
    @ManyToMany(mappedBy = "restaurants")
    private Set<User> users = new HashSet<>();

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Menu> menus = new HashSet<>();

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Ingredient> ingredients = new HashSet<>();

    @AtLeastOneDayOpen
    @OneToOne(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private Settings settings;

    @OneToOne(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private Statistics statistics;

    @OneToOne(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private PricePlan pricePlan;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Invoice> invoices = new HashSet<>();

    @NotNull
    private Instant created;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updated;

    @LastModifiedBy
    private String modifiedBy;

    @CreatedBy
    private String createdBy;

    public Restaurant() {
    }

    public Restaurant(Long id) {
        this.id = id;
    }

    public void addMenu(Menu menu) {
        this.menus.add(menu);
    }

    public void addUser(User user) {
        this.users.add(user);
        user.addRestaurant(this);
    }

    @PrePersist
    protected void prePersist() {
        this.created = Instant.now();
    }

    @PreUpdate
    protected void preUpdate() {
        this.updated = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return name + ", " + address;
    }

}