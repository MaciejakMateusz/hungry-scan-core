package com.hackybear.hungry_scan_core.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Table(name = "feedback")
@Entity
public class Feedback implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer tableId;

    @Min(1)
    @Max(5)
    private Integer service;

    @Min(1)
    @Max(5)
    private Integer food;

    @Min(1)
    @Max(5)
    private Integer vibe;

    private String comment;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime postTime;

    @PrePersist
    private void prePersist() {
        this.postTime = LocalDateTime.now();
    }

}