package pl.rarytas.rarytas_restaurantside.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.rarytas.rarytas_restaurantside.listener.GeneralListener;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Table(name = "restaurant_tables")
@EntityListeners(GeneralListener.class)
@Entity
public class RestaurantTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer number;

    @ManyToOne
    @JoinColumn(name = "section_id", referencedColumnName = "id")
    @JsonIgnore
    private Section section;

    @OneToMany(fetch = FetchType.EAGER)
    private Set<User> users;

    private boolean isActive;

    private boolean billRequested;

    private boolean waiterCalled;

    private int maxNumOfPpl;

    @Column(length = 36, nullable = false)
    @NotNull
    private String token;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updated;

    public void addCustomer(User user) {
        this.users.add(user);
    }

}