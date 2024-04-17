package pl.rarytas.rarytas_restaurantside.test_utils;

import org.springframework.stereotype.Component;
import pl.rarytas.rarytas_restaurantside.entity.*;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.service.interfaces.MenuItemVariantService;
import pl.rarytas.rarytas_restaurantside.service.interfaces.RestaurantService;
import pl.rarytas.rarytas_restaurantside.service.interfaces.RestaurantTableService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides support methods for creating and processing orders.
 */
@Component
public class OrderProcessor {

    private final RestaurantService restaurantService;
    private final RestaurantTableService restaurantTableService;
    private final MenuItemVariantService variantService;

    public OrderProcessor(RestaurantService restaurantService, RestaurantTableService restaurantTableService, MenuItemVariantService variantService) {
        this.restaurantService = restaurantService;
        this.restaurantTableService = restaurantTableService;
        this.variantService = variantService;
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
            totalAmount = totalAmount.add(orderedItem.getMenuItemVariant().getPrice());
        }
        return totalAmount.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Creates dine-in order based on provided parameters.
     *
     * @param tableNumber The number of the table where the order is placed.
     * @param variantsIds The list of IDs of menu item variants to be included in the order.
     * @return The created order.
     */
    public Order createDineInOrder(Integer tableNumber, List<Integer> variantsIds) throws LocalizedException {
        return new CreateOrder(
                restaurantService,
                restaurantTableService,
                variantService,
                1,
                tableNumber,
                variantsIds,
                false).createOrder();
    }

    /**
     * Creates take-away order based on provided parameters.
     *
     * @param menuItemIds The list of IDs of menu items to be included in the order.
     * @return The created order.
     */
    public Order createTakeAwayOrder(List<Integer> menuItemIds) throws LocalizedException {
        return new CreateOrder(
                restaurantService,
                restaurantTableService,
                variantService,
                1,
                19,
                menuItemIds,
                true).createOrder();
    }


    /**
     * A record representing a factory for creating orders.
     */
    private record CreateOrder(
            RestaurantService restaurantService,
            RestaurantTableService restaurantTableService,
            MenuItemVariantService variantService,
            Integer restaurantId,
            Integer restaurantTableId,
            List<Integer> variantsIds,
            boolean isForTakeAway) {

        /**
         * Creates an order based on the provided parameters.
         *
         * @return The created order.
         */
        private Order createOrder() throws LocalizedException {
            Order order = new Order();
            order.setRestaurant(getRestaurant());
            order.setRestaurantTable(getRestaurantTable());
            order.setOrderedItems(createOrderedItems());
//            order.setIsForTakeAway(isForTakeAway);
            //TODO czy order może być na wynos? Czy robimy do tego osobną encję?
            return order;
        }

        /**
         * Retrieves the restaurant associated with this order.
         *
         * @return The restaurant.
         */
        private Restaurant getRestaurant() throws LocalizedException {
            return restaurantService.findById(restaurantId);
        }

        /**
         * Retrieves the restaurant table associated with this order.
         *
         * @return The restaurant table.
         */
        private RestaurantTable getRestaurantTable() throws LocalizedException {
            return restaurantTableService.findById(restaurantTableId);
        }

        /**
         * Creates a list of ordered items based on the provided menu item IDs.
         *
         * @return The list of ordered items.
         */
        private List<OrderedItem> createOrderedItems() {
            List<MenuItemVariant> variants = getMenuItemVariants();
            List<OrderedItem> orderedItems = new ArrayList<>();

            variants.forEach(menuItem -> {
                OrderedItem orderedItem = new OrderedItem();
                orderedItem.setMenuItemVariant(menuItem);
                orderedItem.setQuantity(1); // Default quantity assumed to be 1
                orderedItems.add(orderedItem);
            });
            return orderedItems;
        }

        /**
         * Retrieves the menu item variants associated with this order.
         *
         * @return The list of menu item variants.
         */
        private List<MenuItemVariant> getMenuItemVariants() {
            List<MenuItemVariant> variants = new ArrayList<>();
            this.variantsIds.forEach(id -> {
                MenuItemVariant variant;
                try {
                    variant = variantService.findById(id);
                } catch (LocalizedException e) {
                    throw new RuntimeException(e);
                }
                variants.add(variant);
            });
            return variants;
        }
    }
}