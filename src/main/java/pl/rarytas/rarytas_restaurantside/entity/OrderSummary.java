package pl.rarytas.rarytas_restaurantside.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import pl.rarytas.rarytas_restaurantside.enums.PaymentMethod;
import pl.rarytas.rarytas_restaurantside.listener.OrderSummaryListener;
import pl.rarytas.rarytas_restaurantside.utility.Money;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    @JoinColumn(name = "table_id", referencedColumnName = "id")
    @ManyToOne
    @NotNull
    private RestaurantTable restaurantTable;

    @JoinColumn(name = "restaurant_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    private Restaurant restaurant;

    @Column(length = 50, nullable = false)
    @NotNull
    private LocalDateTime initialOrderTime;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Order> orders;

    @DecimalMin(value = "0.00")
    private BigDecimal tipAmount = Money.of(0.00);

    @DecimalMin(value = "0.00")
    private BigDecimal totalAmount = Money.of(0.00);

    private boolean paid;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
}
