package pl.rarytas.rarytas_restaurantside.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Table(name = "restaurant_tables")
@ToString
public class RestaurantTable {

    @Id
    private Integer id;

    @Column(length = 50, name = "customer_name")
    private String customerName;

}