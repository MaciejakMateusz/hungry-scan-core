package pl.rarytas.rarytas_restaurantside.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.rarytas.rarytas_restaurantside.entity.history.HistoryBooking;

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

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Booking> bookings;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<HistoryBooking> historyBookings;

    private boolean isActive;

    @Column(length = 36, nullable = false)
    @NotNull
    private String token;

    public void removeBooking(Booking booking) {
        if (!this.bookings.isEmpty()) {
            this.bookings.remove(booking);
        }
    }

    public void addBooking(Booking booking) {
        this.bookings.add(booking);
    }

    public void addCustomer(User user) {
        this.users.add(user);
    }
}