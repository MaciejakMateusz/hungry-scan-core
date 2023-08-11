package pl.rarytas.rarytas_restaurantside.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import pl.rarytas.rarytas_restaurantside.annotation.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderedItem> orderedItems;

    @Column(name = "payment_method")
    @PaymentMethod
    private String paymentMethod;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "is_paid", nullable = false)
    private boolean isPaid = false;

    @PrePersist
    private void prePersist() {
        this.orderTime = LocalDateTime.now();
    }

    public String getOrderTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy");
        return orderTime.format(dtf);
    }

    public BigDecimal getTotalAmount() {
        BigDecimal sum = BigDecimal.valueOf(0);
        for (OrderedItem orderedItem : this.orderedItems) {
            sum = sum.add(orderedItem.getMenuItem().getPrice());
        }
        return sum;
    }

}

