package pl.rarytas.hungry_scan_core.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.rarytas.hungry_scan_core.listener.GeneralListener;

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

    @OneToMany(fetch = FetchType.EAGER)
    private Set<User> users;

    @ManyToOne
    private Zone zone;

    private boolean hasQrCode = false;

    private boolean isActive;

    private boolean isVisible = true;

    private boolean billRequested;

    private boolean waiterCalled;

    @Min(value = 1)
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