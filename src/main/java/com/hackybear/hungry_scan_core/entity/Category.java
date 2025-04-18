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

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

@Getter
@Setter
@EqualsAndHashCode
@EntityListeners({AuditingEntityListener.class, GeneralListener.class})
@Table(name = "categories")
@Entity
public class Category implements Comparable<Category>, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull
    private Long menuId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "translatable_name_id", referencedColumnName = "id")
    @DefaultTranslationNotBlank
    @LimitTranslationsLength
    @NotNull
    private Translatable name;

    @OneToMany(mappedBy = "categoryId", cascade = CascadeType.ALL)
    @OrderBy("displayOrder ASC")
    private Set<MenuItem> menuItems = new TreeSet<>();

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

    @Override
    public int compareTo(Category other) {
        return this.displayOrder.compareTo(other.displayOrder);
    }

}