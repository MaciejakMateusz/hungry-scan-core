package pl.rarytas.rarytas_restaurantside.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import pl.rarytas.rarytas_restaurantside.listener.OrderListener;
import pl.rarytas.rarytas_restaurantside.utility.Money;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@EntityListeners(value = OrderListener.class)
@Table(name = "orders")
@Entity
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "table_id", referencedColumnName = "id")
    @ManyToOne
    @NotNull
    private RestaurantTable restaurantTable;

    @JoinColumn(name = "restaurant_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    private Restaurant restaurant;

    @Column(length = 50, nullable = false)
    @NotNull
    private LocalDateTime orderTime;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OrderedItem> orderedItems;

    @DecimalMin(value = "0.00")
    private BigDecimal totalAmount = Money.of(0.00);

    private boolean isForTakeAway;

    private boolean isResolved;

    public BigDecimal getTotalAmount() {
        return this.totalAmount.setScale(2, RoundingMode.HALF_UP);
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount.setScale(2, RoundingMode.HALF_UP);
    }

}