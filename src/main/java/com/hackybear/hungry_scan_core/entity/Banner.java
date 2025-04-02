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

import java.io.Serial;
import java.io.Serializable;

@Slf4j
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Table(name = "banners")
@Entity
public class Banner implements Serializable {

    @Id
    private String id;

    @Serial
    private static final long serialVersionUID = 1L;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "translatable_name_id", referencedColumnName = "id")
    @DefaultTranslationNotBlank
    @LimitTranslationsLength
    @NotNull
    private Translatable name;

}
