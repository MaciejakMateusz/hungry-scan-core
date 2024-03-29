package pl.rarytas.rarytas_restaurantside.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import pl.rarytas.rarytas_restaurantside.enums.PaymentMethod;
import pl.rarytas.rarytas_restaurantside.listener.OrderListener;
import pl.rarytas.rarytas_restaurantside.utility.Money;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@EqualsAndHashCode
@EntityListeners(value = OrderListener.class)
@Table(name = "orders")
@Entity
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "table_id", referencedColumnName = "id")
    @ManyToOne
    @NotNull
    private RestaurantTable restaurantTable;

    @JoinColumn(name = "restaurant_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    private Restaurant restaurant;

    @Column(name = "order_time", length = 50, nullable = false)
    @NotNull
    private LocalDateTime orderTime;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OrderedItem> orderedItems;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column(name = "total_amount")
    @DecimalMin(value = "0.00")
    private BigDecimal totalAmount = Money.of(0.00);

    @Column(name = "tip_amount")
    @DecimalMin(value = "0.00")
    private BigDecimal tipAmount = Money.of(0.00);

    @Column(name = "is_paid")
    private boolean paid;

    @Column(name = "take_away")
    private boolean forTakeAway;

    @Column(name = "is_resolved")
    private boolean isResolved;

    @Column(name = "order_number")
    private Integer orderNumber;

    public void addToOrderedItems(List<OrderedItem> newItems) {
        for (OrderedItem newItem : newItems) {
            boolean found = false;
            for (OrderedItem existingItem : this.orderedItems) {
                if (Objects.equals(existingItem.getMenuItemVariant().getId(), newItem.getMenuItemVariant().getId())) {
                    existingItem.setQuantity(existingItem.getQuantity() + 1);
                    found = true;
                    break;
                }
            }
            if (!found) {
                this.orderedItems.add(newItem);
            }
        }
    }

    public BigDecimal getTotalAmount() {
        return this.totalAmount.setScale(2, RoundingMode.HALF_UP);
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTipAmount() {
        return this.tipAmount.setScale(2, RoundingMode.HALF_UP);
    }

    public void setTipAmount(BigDecimal tipAmount) {
        this.tipAmount = tipAmount.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", restaurantTable=" + restaurantTable +
                ", restaurant=" + restaurant +
                ", orderTime=" + orderTime +
                ", orderedItems=" + orderedItems +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", totalAmount=" + totalAmount +
                ", isPaid=" + paid +
                '}';
    }
}