package com.hackybear.hungry_scan_core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Slf4j
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Table(name = "menu_item_view_events")
@Entity
public class MenuItemViewEvent implements Serializable, Comparable<MenuItemViewEvent> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(nullable = false)
    @NotNull
    private Long menuId;

    @Column(nullable = false)
    @NotNull
    private Long menuItemId;

    @Column(nullable = false)
    @NotNull
    private LocalDateTime viewedAt;

    @PrePersist
    public void prePersist() {
        this.viewedAt = LocalDateTime.now();
    }

    @Override
    public int compareTo(MenuItemViewEvent o) {
        return this.viewedAt.compareTo(o.viewedAt);
    }
}
