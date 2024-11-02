package com.hackybear.hungry_scan_core.entity;

import com.hackybear.hungry_scan_core.listener.GeneralListener;
import com.hackybear.hungry_scan_core.utility.TimeRange;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode
@Table(name = "schedule")
@EntityListeners({AuditingEntityListener.class, GeneralListener.class})
@Entity
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Menu menu;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Map<DayOfWeek, TimeRange> plan = new HashMap<>();

}
