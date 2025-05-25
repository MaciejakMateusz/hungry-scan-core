package com.hackybear.hungry_scan_core.entity;

import com.hackybear.hungry_scan_core.annotation.AnyTranslationNotBlank;
import com.hackybear.hungry_scan_core.annotation.LimitTranslationsLength;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "translatable_displayed_name_id", referencedColumnName = "id")
    @AnyTranslationNotBlank
    @LimitTranslationsLength
    @NotNull
    private Translatable displayedName;

}