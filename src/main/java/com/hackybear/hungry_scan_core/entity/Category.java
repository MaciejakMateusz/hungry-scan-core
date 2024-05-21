package com.hackybear.hungry_scan_core.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hackybear.hungry_scan_core.annotation.DefaultTranslationNotBlank;
import com.hackybear.hungry_scan_core.listener.GeneralListener;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.LocalDateTime;
import java.util.Locale;

@Getter
@Setter
@EqualsAndHashCode
@EntityListeners(GeneralListener.class)
@Table(name = "categories")
@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "translatable_name_id", referencedColumnName = "id")
    @DefaultTranslationNotBlank
    @NotNull
    private Translatable name;

    private boolean isAvailable = true;

    private boolean isBarServed;

    @Column(nullable = false)
    @NotNull
    private Integer displayOrder;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updated;

    @Override
    public String toString() {
        Locale locale = LocaleContextHolder.getLocale();
        return locale.getISO3Language().equals("pl") ? name.getDefaultTranslation() : name.getTranslationEn();
    }
}
