package com.hackybear.hungry_scan_core.entity;

import com.hackybear.hungry_scan_core.annotation.CollectionNotEmpty;
import com.hackybear.hungry_scan_core.listener.BookingListener;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Setter
@Getter
@EqualsAndHashCode
@EntityListeners(value = BookingListener.class)
@Table(name = "bookings")
@Entity
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotNull
    private LocalDate date;

    @Column(nullable = false)
    @NotNull
    private LocalTime time;

    @Column(nullable = false)
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
    @CollectionNotEmpty
    private Set<RestaurantTable> restaurantTables;

    private Byte numTablesBooked;
}