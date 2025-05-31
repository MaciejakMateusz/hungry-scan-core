package com.hackybear.hungry_scan_core.entity.history;

import com.hackybear.hungry_scan_core.entity.Restaurant;
import com.hackybear.hungry_scan_core.entity.RestaurantTable;
import com.hackybear.hungry_scan_core.enums.PaymentMethod;
import com.hackybear.hungry_scan_core.utility.Money;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@Table(name = "history_order_summary")
@Entity
public class HistoryOrderSummary implements Serializable {

    public HistoryOrderSummary(Long id,
                               Restaurant restaurant,
                               RestaurantTable restaurantTable,
                               LocalDate initialOrderDate,
                               LocalTime initialOrderTime,
                               BigDecimal tipAmount,
                               BigDecimal totalAmount,
                               boolean paid,
                               boolean isBillSplitRequested,
                               PaymentMethod paymentMethod) {
        this.id = id;
        this.restaurant = restaurant;
        this.restaurantTable = restaurantTable;
        this.initialOrderDate = initialOrderDate;
        this.initialOrderTime = initialOrderTime;
        this.tipAmount = tipAmount;
        this.totalAmount = totalAmount;
        this.paid = paid;
        this.isBillSplitRequested = isBillSplitRequested;
        this.paymentMethod = paymentMethod;
    }

    @Id
    Long id;

    @Serial
    private static final long serialVersionUID = 1L;

    @JoinColumn(name = "restaurant_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    private Restaurant restaurant;

    @JoinColumn(name = "table_id", referencedColumnName = "id")
    @ManyToOne
    @NotNull
    private RestaurantTable restaurantTable;

    @Column(length = 50, nullable = false)
    private LocalDate initialOrderDate;

    @Column(length = 50, nullable = false)
    private LocalTime initialOrderTime;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private List<HistoryOrder> historyOrders = new ArrayList<>();

    @DecimalMin(value = "0.00")
    private BigDecimal tipAmount = Money.of(0.00);

    @DecimalMin(value = "0.00")
    private BigDecimal totalAmount = Money.of(0.00);

    private boolean paid;

    private boolean isBillSplitRequested;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
}
