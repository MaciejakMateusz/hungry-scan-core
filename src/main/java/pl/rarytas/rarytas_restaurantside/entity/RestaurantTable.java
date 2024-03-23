package pl.rarytas.rarytas_restaurantside.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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

    @OneToMany(fetch = FetchType.EAGER)
    private Set<User> users;

    private boolean isActive;

    @Column(length = 36, nullable = false)
    @NotNull
    private String token;

    public void addCustomer(User user) {
        this.users.add(user);
    }
}