package pl.rarytas.rarytas_restaurantside.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import pl.rarytas.rarytas_restaurantside.listener.GeneralListener;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Table(name = "sections")
@EntityListeners(GeneralListener.class)
@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    private String name;

    @OneToMany(mappedBy = "section")
    private List<RestaurantTable> restaurantTables;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updated;

}
