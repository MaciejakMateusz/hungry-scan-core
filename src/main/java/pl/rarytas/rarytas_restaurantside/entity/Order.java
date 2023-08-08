package pl.rarytas.rarytas_restaurantside.entity;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
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
    private RestaurantTable restaurantTable;

    @JoinColumn(name = "restaurant_id", referencedColumnName = "id")
    @ManyToOne
    private Restaurant restaurant;

    @Column(name = "order_time", nullable = false)
    private LocalDateTime orderTime;

    @OneToMany
    private List<OrderedItem> orderedItems;

    @Column(name = "payment_method")
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