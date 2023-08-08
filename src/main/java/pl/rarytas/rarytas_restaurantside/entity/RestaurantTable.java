package pl.rarytas.rarytas_restaurantside.entity;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "restaurant_table")
public class RestaurantTable {

    @Id
    private Integer id;

    @Column(length = 50, name = "customer_name")
    private String customerName;

    @OneToMany(mappedBy = "restaurantTable", cascade = CascadeType.ALL)
    private List<Order> orders;
}