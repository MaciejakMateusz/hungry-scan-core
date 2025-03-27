package com.hackybear.hungry_scan_core.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hackybear.hungry_scan_core.listener.GeneralListener;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
import java.util.Set;
import java.util.TreeSet;

@Getter
@Setter
@EqualsAndHashCode()
@Table(name = "menus")
@EntityListeners({AuditingEntityListener.class, GeneralListener.class})
@Entity
public class Menu implements Serializable, Comparable<Menu> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(length = 100, nullable = false)
    @NotBlank
    private String name;

    @Column(name = "restaurant_id", nullable = false)
    @NotNull
    private Long restaurantId;

    @OneToMany(mappedBy = "menuId")
    @OrderBy("displayOrder ASC")
    private Set<Category> categories = new TreeSet<>();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Schedule schedule;

    @Column(name = "standard")
    private boolean standard;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updated;

    @LastModifiedBy
    private String modifiedBy;

    @CreatedBy
    private String createdBy;

    @Override
    public int compareTo(Menu other) {
        return this.getName().compareTo(other.getName());
    }
}
