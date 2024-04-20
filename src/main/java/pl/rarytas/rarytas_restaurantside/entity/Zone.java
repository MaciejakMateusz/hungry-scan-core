package pl.rarytas.rarytas_restaurantside.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import pl.rarytas.rarytas_restaurantside.listener.GeneralListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Table(name = "zones")
@EntityListeners(GeneralListener.class)
@Entity
public class Zone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    private String name;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private Set<RestaurantTable> restaurantTables = new HashSet<>();

    @Min(1)
    private Integer displayOrder;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updated;

    public void addRestaurantTable(RestaurantTable restaurantTable) {
        this.restaurantTables.add(restaurantTable);
    }

    public void removeRestaurantTable(RestaurantTable restaurantTable) {
        this.restaurantTables.remove(restaurantTable);
    }

}
