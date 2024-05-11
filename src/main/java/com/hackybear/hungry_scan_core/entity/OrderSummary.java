package com.hackybear.hungry_scan_core.entity;

import com.hackybear.hungry_scan_core.enums.PaymentMethod;
import com.hackybear.hungry_scan_core.listener.OrderSummaryListener;
import com.hackybear.hungry_scan_core.utility.Money;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@EntityListeners(value = OrderSummaryListener.class)
@Table(name = "order_summaries")
@Entity
public class OrderSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @JoinColumn(name = "restaurant_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    private Restaurant restaurant;

    @JoinColumn(name = "table_id", referencedColumnName = "id")
    @ManyToOne
    private RestaurantTable restaurantTable;

    @Column(length = 50, nullable = false)
    private LocalDate initialOrderDate;

    @Column(length = 50, nullable = false)
    private LocalTime initialOrderTime;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Order> orders = new ArrayList<>();

    @DecimalMin(value = "0.00")
    private BigDecimal tipAmount = Money.of(0.00);

    @DecimalMin(value = "0.00")
    private BigDecimal totalAmount = Money.of(0.00);

    private boolean paid;

    private boolean isBillSplitRequested;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    public void addOrder(Order order) {
        this.orders.add(order);
    }

}
