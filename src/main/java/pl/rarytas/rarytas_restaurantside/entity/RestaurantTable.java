package pl.rarytas.rarytas_restaurantside.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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

}