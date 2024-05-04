package pl.rarytas.hungry_scan_core.entity.history;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import pl.rarytas.hungry_scan_core.entity.Restaurant;
import pl.rarytas.hungry_scan_core.entity.RestaurantTable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "history_orders")
@Slf4j
public class HistoryOrder {

    public HistoryOrder(Long id,
                        Restaurant restaurant,
                        RestaurantTable restaurantTable,
                        LocalDate orderDate,
                        LocalTime orderTime,
                        BigDecimal totalAmount,
                        boolean isResolved) {
        this.id = id;
        this.restaurant = restaurant;
        this.restaurantTable = restaurantTable;
        this.orderDate = orderDate;
        this.orderTime = orderTime;
        setTotalAmount(totalAmount);
        this.isResolved = isResolved;
    }

    @Id
    private Long id;

    @JoinColumn(name = "table_id", referencedColumnName = "id")
    @ManyToOne
    @NotNull
    private RestaurantTable restaurantTable;

    @JoinColumn(name = "restaurant_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    private Restaurant restaurant;

    @Column(nullable = false)
    @NotNull
    private LocalDate orderDate;

    @Column(nullable = false)
    @NotNull
    private LocalTime orderTime;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<HistoryOrderedItem> historyOrderedItems;

    @DecimalMin(value = "0.00")
    private BigDecimal totalAmount;

    private boolean isForTakeAway;

    private boolean isResolved;

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount.setScale(2, RoundingMode.HALF_UP);
    }

}