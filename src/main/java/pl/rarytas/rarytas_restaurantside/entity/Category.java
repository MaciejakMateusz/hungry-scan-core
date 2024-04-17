package pl.rarytas.rarytas_restaurantside.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import pl.rarytas.rarytas_restaurantside.annotation.SizeIfNotEmpty;
import pl.rarytas.rarytas_restaurantside.listener.GeneralListener;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@EntityListeners(GeneralListener.class)
@Table(name = "categories")
@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 100, nullable = false)
    @NotBlank
    private String name;

    @SizeIfNotEmpty
    @Length(max = 300)
    @Column(length = 300)
    private String description;

    @OneToMany(mappedBy = "category")
    private List<MenuItem> menuItems;

    private boolean isAvailable = true;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updated;

    @Override
    public String toString() {
        return name;
    }
}
