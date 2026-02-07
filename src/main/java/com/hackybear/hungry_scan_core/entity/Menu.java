package com.hackybear.hungry_scan_core.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hackybear.hungry_scan_core.annotation.AnyTranslationNotBlank;
import com.hackybear.hungry_scan_core.annotation.LimitTranslationsLength;
import com.hackybear.hungry_scan_core.enums.Theme;
import com.hackybear.hungry_scan_core.listener.GeneralListener;
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
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(
        name = "menus",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_menu_restaurant_name",
                columnNames = {"restaurant_id", "name"}
        )
)
@EntityListeners({AuditingEntityListener.class, GeneralListener.class})
@Entity
public class Menu implements Serializable, Comparable<Menu> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(length = 100, nullable = false)
    @NotBlank
    @Length(max = 100)
    private String name;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    @JsonIgnore
    private Restaurant restaurant;

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    private Set<Category> categories = new HashSet<>();

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MenuPlan> plan = new HashSet<>();

    @Column(name = "standard")
    private boolean standard;

    @Column(name = "banner_icon_visible")
    private boolean bannerIconVisible = true;

    @Enumerated(EnumType.STRING)
    private Theme theme;

    @ManyToOne
    @NotNull
    private MenuColor color;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "translatable_message_id", referencedColumnName = "id", nullable = false)
    @AnyTranslationNotBlank
    @LimitTranslationsLength
    @NotNull
    private Translatable message;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updated;

    @LastModifiedBy
    private String modifiedBy;

    @CreatedBy
    private String createdBy;

    public Menu() {
    }

    public Menu(Long id) {
        this.id = id;
    }

    @Override
    public int compareTo(Menu other) {
        return this.getName().compareTo(other.getName());
    }
}
