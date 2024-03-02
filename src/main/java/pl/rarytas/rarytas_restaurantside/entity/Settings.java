package pl.rarytas.rarytas_restaurantside.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import pl.rarytas.rarytas_restaurantside.enums.Language;

import java.time.LocalTime;

@Getter
@Setter
@Table(name = "settings")
@Entity
public class Settings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(nullable = false)
    private LocalTime openingTime;

    @NotNull
    @Column(nullable = false)
    private LocalTime closingTime;

    @NotNull
    @Column(nullable = false)
    private Long bookingDuration;

    @NotNull
    @Column(nullable = false)
    private Language language = Language.PL;

    @NotNull
    @Column(nullable = false)
    private Short capacity;
}