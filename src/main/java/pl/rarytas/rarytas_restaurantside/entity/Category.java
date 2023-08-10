package pl.rarytas.rarytas_restaurantside.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

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
    @NotEmpty
    @NotNull
    private String name;

    @Column
    @Length(min = 10, message = "Opis kategorii musi być dłuższy niż 5 znaków")
    private String description;

    @OneToMany(mappedBy = "category", fetch = FetchType.EAGER)
    private List<MenuItem> menuItems;

    @Override
    public String toString() {
        return name;
    }
}
