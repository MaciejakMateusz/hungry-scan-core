package com.hackybear.hungry_scan_core.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hackybear.hungry_scan_core.listener.GeneralListener;
import com.hackybear.hungry_scan_core.utility.TimeRange;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
@EntityListeners({AuditingEntityListener.class, GeneralListener.class})
@Entity
@Table(name = "menu_plans")
public class MenuPlan implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Serial
    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "menu_id", nullable = false)
    @JsonIgnore
    private Menu menu;

    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    @ElementCollection
    private Set<TimeRange> timeRanges = new HashSet<>();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updated;

    @LastModifiedBy
    private String modifiedBy;

    @CreatedBy
    private String createdBy;

}
