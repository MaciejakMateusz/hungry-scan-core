package pl.rarytas.rarytas_restaurantside.testSupport;

import org.springframework.stereotype.Component;
import pl.rarytas.rarytas_restaurantside.entity.*;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.service.interfaces.MenuItemService;
import pl.rarytas.rarytas_restaurantside.service.interfaces.RestaurantService;
import pl.rarytas.rarytas_restaurantside.service.interfaces.RestaurantTableService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides support methods for creating and processing orders.
 */
@Component
public class OrderProcessor {

    private final RestaurantService restaurantService;
    private final RestaurantTableService restaurantTableService;
    private final MenuItemService menuItemService;

    public OrderProcessor(RestaurantService restaurantService, RestaurantTableService restaurantTableService, MenuItemService menuItemService) {
        this.restaurantService = restaurantService;
        this.restaurantTableService = restaurantTableService;
        this.menuItemService = menuItemService;
    }

    /**
     * Calculates the total amount of an order based on a list of ordered items.
     *
     * @param orderedItems The list of ordered items.
     * @return The total amount of the order as a BigDecimal.
     */
    public BigDecimal countTotalAmount(List<OrderedItem> orderedItems) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderedItem orderedItem : orderedItems) {
            totalAmount = totalAmount.add(orderedItem.getMenuItem().getPrice());
        }
        return totalAmount;
    }

    /**
     * Creates an order based on provided parameters.
     *
     * @param tableNumber The number of the table where the order is placed.
     * @param menuItemIds The list of IDs of menu items to be included in the order.
     * @param forTakeAway A boolean indicating whether the order is for takeaway.
     * @return The created order.
     */
    public Order getCreatedOrder(Integer tableNumber, List<Integer> menuItemIds, boolean forTakeAway) {
        return new CreateOrder(
                restaurantService,
                restaurantTableService,
                menuItemService,
                1,
                tableNumber,
                menuItemIds,
                forTakeAway).createOrder();
    }

    /**
     * A record representing a factory for creating orders.
     */
    private record CreateOrder(
            RestaurantService restaurantService,
            RestaurantTableService restaurantTableService,
            MenuItemService menuItemService,
            Integer restaurantId,
            Integer restaurantTableId,
            List<Integer> menuItemIds,
            boolean isForTakeAway) {

        /**
         * Creates an order based on the provided parameters.
         *
         * @return The created order.
         */
        private Order createOrder() {
            Order order = new Order();
            order.setRestaurant(getRestaurant());
            order.setRestaurantTable(getRestaurantTable());
            order.setOrderedItems(createOrderedItems());
            order.setForTakeAway(isForTakeAway);
            return order;
        }

        /**
         * Retrieves the restaurant associated with this order.
         *
         * @return The restaurant.
         */
        private Restaurant getRestaurant() {
            return restaurantService.findById(restaurantId).orElseThrow();
        }

        /**
         * Retrieves the restaurant table associated with this order.
         *
         * @return The restaurant table.
         */
        private RestaurantTable getRestaurantTable() {
            return restaurantTableService.findById(restaurantTableId).orElseThrow();
        }

        /**
         * Creates a list of ordered items based on the provided menu item IDs.
         *
         * @return The list of ordered items.
         */
        private List<OrderedItem> createOrderedItems() {
            List<MenuItem> menuItems = getMenuItems();
            List<OrderedItem> orderedItems = new ArrayList<>();

            menuItems.forEach(menuItem -> {
                OrderedItem orderedItem = new OrderedItem();
                orderedItem.setMenuItem(menuItem);
                orderedItem.setQuantity(1); // Default quantity assumed to be 1
                orderedItems.add(orderedItem);
            });
            return orderedItems;
        }

        /**
         * Retrieves the menu items associated with this order.
         *
         * @return The list of menu items.
         */
        private List<MenuItem> getMenuItems() {
            List<MenuItem> menuItems = new ArrayList<>();
            this.menuItemIds.forEach(id -> {
                MenuItem menuItem;
                try {
                    menuItem = menuItemService.findById(id);
                } catch (LocalizedException e) {
                    throw new RuntimeException(e);
                }
                menuItems.add(menuItem);
            });
            return menuItems;
        }
    }
}