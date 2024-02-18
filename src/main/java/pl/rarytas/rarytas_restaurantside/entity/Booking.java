package pl.rarytas.rarytas_restaurantside.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import pl.rarytas.rarytas_restaurantside.enums.DayPart;
import pl.rarytas.rarytas_restaurantside.listener.OrderListener;

import java.time.LocalDate;
import java.time.LocalTime;

@Setter
@Getter
@EqualsAndHashCode
@EntityListeners(value = OrderListener.class)
@Table(name = "bookings")
@Entity
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(nullable = false)
    @NotNull
    private LocalDate date;

    @Column(nullable = false)
    @NotNull
    private LocalTime time;

    @Column(nullable = false)
    @NotNull
    @Min(1)
    private Integer numOfPpl;

    @Column(nullable = false)
    @NotNull
    private String surname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private DayPart dayPart;
}
