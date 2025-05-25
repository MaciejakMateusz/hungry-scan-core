package com.hackybear.hungry_scan_core.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hackybear.hungry_scan_core.listener.GeneralListener;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@Table(name = "translatable")
@EntityListeners({AuditingEntityListener.class, GeneralListener.class})
@Entity
public class Translatable implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Serial
    private static final long serialVersionUID = 1L;

    private String pl;

    private String en;

    private String fr;

    private String de;

    private String es;

    private String uk;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updated;

    @LastModifiedBy
    private String modifiedBy;

    @CreatedBy
    private String createdBy;

    public Translatable withId(Long id) {
        this.id = id;
        return this;
    }

    public Translatable withPl(String translation) {
        this.pl = translation;
        return this;
    }

    public Translatable withEn(String translation) {
        this.en = translation;
        return this;
    }

    public Translatable withFr(String translation) {
        this.fr = translation;
        return this;
    }

    public Translatable withDe(String translation) {
        this.de = translation;
        return this;
    }

    public Translatable withEs(String translation) {
        this.es = translation;
        return this;
    }

    public Translatable withUk(String translation) {
        this.uk = translation;
        return this;
    }

}