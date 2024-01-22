package pl.rarytas.rarytas_restaurantside.entity.archive;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import pl.rarytas.rarytas_restaurantside.annotation.PaymentMethod;
import pl.rarytas.rarytas_restaurantside.entity.OrderedItem;
import pl.rarytas.rarytas_restaurantside.entity.Restaurant;
import pl.rarytas.rarytas_restaurantside.entity.RestaurantTable;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "history_orders")
@Slf4j
public class HistoryOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "table_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    private RestaurantTable restaurantTable;

    @JoinColumn(name = "restaurant_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    private Restaurant restaurant;

    @Column(name = "order_time", length = 50, nullable = false)
    @NotNull
    private String orderTime;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<OrderedItem> orderedItems;

    @Column(name = "payment_method")
    @PaymentMethod
    private String paymentMethod;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "is_paid")
    private boolean paid;

    @Column(name = "take_away")
    private boolean forTakeAway;

    @Column(name = "bill_requested")
    private boolean billRequested;

    @Column(name = "is_resolved")
    private boolean isResolved;

    @Column(name = "waiter_called")
    private boolean waiterCalled;

    @Column(name = "order_number")
    private Integer orderNumber;

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", restaurantTable=" + restaurantTable +
                ", restaurant=" + restaurant +
                ", orderTime=" + orderTime +
                ", orderedItems=" + orderedItems +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", totalAmount=" + totalAmount +
                ", isPaid=" + paid +
                '}';
    }
}