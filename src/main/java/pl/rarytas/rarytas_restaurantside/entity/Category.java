package pl.rarytas.rarytas_restaurantside.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import pl.rarytas.rarytas_restaurantside.annotation.SizeIfNotEmpty;

import java.time.LocalDateTime;
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
    @NotBlank
    private String name;

    @SizeIfNotEmpty
    private String description;

    @OneToMany(mappedBy = "category", fetch = FetchType.EAGER)
    private List<MenuItem> menuItems;

    private LocalDateTime created;
    private LocalDateTime updated;

    @PrePersist
    private void prePersist() {
        this.created = LocalDateTime.now();
    }

    @PreUpdate
    private void preUpdate() {
        this.updated = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return name;
    }
}
