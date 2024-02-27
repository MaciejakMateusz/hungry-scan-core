package pl.rarytas.rarytas_restaurantside.entity.archive;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import pl.rarytas.rarytas_restaurantside.annotation.PaymentMethod;
import pl.rarytas.rarytas_restaurantside.entity.Restaurant;
import pl.rarytas.rarytas_restaurantside.entity.RestaurantTable;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "history_orders")
@Slf4j
public class HistoryOrder {

    public HistoryOrder(Long id,
                        RestaurantTable restaurantTable,
                        Restaurant restaurant, String orderTime,
                        String paymentMethod,
                        BigDecimal totalAmount,
                        boolean paid,
                        boolean forTakeAway,
                        boolean billRequested,
                        boolean isResolved,
                        boolean waiterCalled,
                        Integer orderNumber) {
        this.id = id;
        this.restaurantTable = restaurantTable;
        this.restaurant = restaurant;
        this.orderTime = orderTime;
        this.paymentMethod = paymentMethod;
        this.totalAmount = totalAmount;
        this.paid = paid;
        this.forTakeAway = forTakeAway;
        this.billRequested = billRequested;
        this.isResolved = isResolved;
        this.waiterCalled = waiterCalled;
        this.orderNumber = orderNumber;
    }

    @Id
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
    private List<HistoryOrderedItem> historyOrderedItems;

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
                ", orderedItems=" + historyOrderedItems +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", totalAmount=" + totalAmount +
                ", isPaid=" + paid +
                '}';
    }

}