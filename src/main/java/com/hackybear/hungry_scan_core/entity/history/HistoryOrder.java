package com.hackybear.hungry_scan_core.entity.history;

import com.hackybear.hungry_scan_core.entity.Restaurant;
import com.hackybear.hungry_scan_core.entity.RestaurantTable;
import com.hackybear.hungry_scan_core.utility.Money;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
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
    private RestaurantTable restaurantTable;

    @JoinColumn(name = "restaurant_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Restaurant restaurant;

    @Column(nullable = false)
    private LocalDate orderDate;

    @Column(nullable = false)
    private LocalTime orderTime;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private List<HistoryOrderedItem> historyOrderedItems = new ArrayList<>();

    @DecimalMin(value = "0.00")
    private BigDecimal totalAmount = Money.of(0.00);

    private boolean isForTakeAway;

    private boolean isResolved;

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount.setScale(2, RoundingMode.HALF_UP);
    }

}