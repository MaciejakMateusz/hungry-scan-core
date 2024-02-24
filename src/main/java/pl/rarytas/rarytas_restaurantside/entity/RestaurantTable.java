package pl.rarytas.rarytas_restaurantside.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Table(name = "restaurant_tables")
@Entity
public class RestaurantTable {

    @Id
    private Integer id;

    @Column(length = 50, name = "customer_name")
    private String customerName;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Booking> bookings;
}