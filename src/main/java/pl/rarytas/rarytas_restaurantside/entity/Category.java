package pl.rarytas.rarytas_restaurantside.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 100, nullable = false)
    private String name;

    @Column
    private String description;

    @OneToMany(mappedBy = "category", fetch = FetchType.EAGER)
    private List<MenuItem> menuItems;

    @Override
    public String toString() {
        return name;
    }
}
