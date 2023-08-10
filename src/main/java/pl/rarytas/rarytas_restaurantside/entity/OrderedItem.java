package pl.rarytas.rarytas_restaurantside.entity;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "ordered_items")
public class OrderedItem implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "ordered_item_id", referencedColumnName = "id")
    private MenuItem menuItem;

    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Order order;

    @Column(nullable = false)
    @Min(value = 1, message = "Ilość musi wynosić minimum 1")
    @NotNull
    @NotEmpty
    private Integer quantity;
}