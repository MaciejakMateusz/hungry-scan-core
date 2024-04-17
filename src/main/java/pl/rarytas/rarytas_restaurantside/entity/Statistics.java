package pl.rarytas.rarytas_restaurantside.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Table(name = "statistics")
@Entity
public class Statistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalTime rushHour;

    private LocalTime avgStayTime;

    private LocalTime avgWaitTime;

    @Transient
    private List<MenuItem> menuItems = new ArrayList<>();

}
