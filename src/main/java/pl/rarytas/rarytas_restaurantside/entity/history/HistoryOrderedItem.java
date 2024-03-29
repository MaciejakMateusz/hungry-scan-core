package pl.rarytas.rarytas_restaurantside.entity.history;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.rarytas.rarytas_restaurantside.entity.MenuItem;
import pl.rarytas.rarytas_restaurantside.entity.MenuItemVariant;

import java.io.Serializable;

@Getter
@Setter
@Entity
@ToString
@Table(name = "history_ordered_items")
public class HistoryOrderedItem implements Serializable {

    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "menu_item_id", referencedColumnName = "id")
    private MenuItemVariant menuItemVariant;

    @Column(nullable = false)
    @Min(value = 1, message = "Ilość musi wynosić minimum 1")
    @NotNull
    private Integer quantity;

    boolean isReadyToServe;
}