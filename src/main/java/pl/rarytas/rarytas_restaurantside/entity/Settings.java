package pl.rarytas.rarytas_restaurantside.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@Table(name = "settings")
@Entity
public class Settings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @OneToOne
    Restaurant restaurant;

    @NotNull
    @Column(nullable = false)
    LocalTime openingTime;

    @NotNull
    @Column(nullable = false)
    LocalTime closingTime;

    @NotNull
    @Column(nullable = false)
    LocalTime bookingDuration;
}