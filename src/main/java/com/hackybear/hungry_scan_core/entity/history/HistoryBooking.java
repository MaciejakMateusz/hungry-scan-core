package com.hackybear.hungry_scan_core.entity.history;

import com.hackybear.hungry_scan_core.entity.Booking;
import com.hackybear.hungry_scan_core.entity.RestaurantTable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "history_bookings")
@Entity
public class HistoryBooking implements Serializable {

    public HistoryBooking(Booking booking) {
        this.id = booking.getId();
        this.date = booking.getDate();
        this.time = booking.getTime();
        this.expirationTime = booking.getExpirationTime();
        this.numOfPpl = booking.getNumOfPpl();
        this.surname = booking.getSurname();
        this.restaurantTables = booking.getRestaurantTables();
        this.numTablesBooked = booking.getNumTablesBooked();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(nullable = false)
    @NotNull
    private LocalDate date;

    @Column(nullable = false)
    @NotNull
    private LocalTime time;

    @Column(nullable = false)
    @NotNull
    private LocalTime expirationTime;

    @Column(nullable = false)
    @NotNull
    @Min(1)
    private Short numOfPpl;

    @Column(nullable = false)
    @NotNull
    private String surname;

    @Column(nullable = false)
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<RestaurantTable> restaurantTables;

    private Byte numTablesBooked;
}