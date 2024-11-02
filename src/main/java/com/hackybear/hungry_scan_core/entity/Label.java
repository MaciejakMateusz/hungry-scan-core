package com.hackybear.hungry_scan_core.entity;

import com.hackybear.hungry_scan_core.annotation.DefaultTranslationNotBlank;
import com.hackybear.hungry_scan_core.annotation.LimitTranslationsLength;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Table(name = "labels")
@Entity
public class Label {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "restaurant_id", nullable = false)
    @NotNull
    private Long restaurantId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "translatable_name_id", referencedColumnName = "id")
    @DefaultTranslationNotBlank
    @LimitTranslationsLength
    @NotNull
    private Translatable name;

    private String iconName;
}
