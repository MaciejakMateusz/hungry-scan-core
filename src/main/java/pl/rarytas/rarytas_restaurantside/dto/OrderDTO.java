package pl.rarytas.rarytas_restaurantside.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import pl.rarytas.rarytas_restaurantside.entity.OrderedItem;
import pl.rarytas.rarytas_restaurantside.entity.Restaurant;
import pl.rarytas.rarytas_restaurantside.entity.RestaurantTable;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Slf4j
public class OrderDTO {

    private Long id;

    private RestaurantTable restaurantTable;

    private Restaurant restaurant;

    private String orderTime;

    private List<OrderedItem> orderedItems;

    private String paymentMethod;

    private BigDecimal totalAmount;

    private boolean paid;

    private boolean forTakeAway;

    private boolean billRequested;

    private boolean isResolved;

    private boolean waiterCalled;

    private Integer orderNumber;

}
