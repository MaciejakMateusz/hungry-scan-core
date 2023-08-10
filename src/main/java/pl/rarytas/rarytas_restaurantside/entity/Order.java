package pl.rarytas.rarytas_restaurantside.entity;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import pl.rarytas.rarytas_restaurantside.annotation.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JoinColumn(name = "table_id", referencedColumnName = "id")
    @ManyToOne
    @NotNull
    private RestaurantTable restaurantTable;

    @JoinColumn(name = "restaurant_id", referencedColumnName = "id")
    @ManyToOne
    @NotNull
    private Restaurant restaurant;

    @Column(name = "order_time", nullable = false)
    @NotNull
    private LocalDateTime orderTime;

    @OneToMany(fetch = FetchType.EAGER)
    private List<OrderedItem> orderedItems;

    @Column(name = "payment_method")
    @PaymentMethod
    private String paymentMethod;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "is_paid", nullable = false)
    private boolean isPaid = false;

    @PrePersist
    private void prePersist() {
        this.orderTime = LocalDateTime.now();
    }
}