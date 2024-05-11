package com.hackybear.hungry_scan_core.entity.history;

import com.hackybear.hungry_scan_core.entity.Feedback;
import com.hackybear.hungry_scan_core.entity.Restaurant;
import com.hackybear.hungry_scan_core.entity.RestaurantTable;
import com.hackybear.hungry_scan_core.enums.PaymentMethod;
import com.hackybear.hungry_scan_core.listener.OrderSummaryListener;
import com.hackybear.hungry_scan_core.utility.Money;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@EntityListeners(value = OrderSummaryListener.class)
@NoArgsConstructor
@Table(name = "order_summaries")
@Entity
public class HistoryOrderSummary {

    public HistoryOrderSummary(Long id,
                               RestaurantTable restaurantTable,
                               Restaurant restaurant,
                               LocalDate initialOrderDate,
                               LocalTime initialOrderTime,
                               BigDecimal tipAmount,
                               BigDecimal totalAmount,
                               boolean paid,
                               PaymentMethod paymentMethod) {
        this.id = id;
        this.restaurantTable = restaurantTable;
        this.restaurant = restaurant;
        this.initialOrderDate = initialOrderDate;
        this.initialOrderTime = initialOrderTime;
        this.tipAmount = tipAmount;
        this.totalAmount = totalAmount;
        this.paid = paid;
        this.paymentMethod = paymentMethod;
    }

    @Id
    Long id;

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
    private LocalDate initialOrderDate;

    @Column(nullable = false)
    @NotNull
    private LocalTime initialOrderTime;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<HistoryOrder> historyOrders;

    @DecimalMin(value = "0.00")
    private BigDecimal tipAmount = Money.of(0.00);

    @DecimalMin(value = "0.00")
    private BigDecimal totalAmount = Money.of(0.00);

    private boolean paid;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @OneToOne(cascade = CascadeType.MERGE)
    private Feedback feedback;
}
