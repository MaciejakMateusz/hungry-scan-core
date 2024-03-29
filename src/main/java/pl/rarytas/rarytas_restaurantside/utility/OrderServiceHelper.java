package pl.rarytas.rarytas_restaurantside.utility;

import org.springframework.stereotype.Component;
import pl.rarytas.rarytas_restaurantside.entity.Order;
import pl.rarytas.rarytas_restaurantside.entity.OrderedItem;
import pl.rarytas.rarytas_restaurantside.exception.ExceptionHelper;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.repository.OrderRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@Component
public class OrderServiceHelper {

    private final OrderRepository orderRepository;
    private final ExceptionHelper exceptionHelper;

    public OrderServiceHelper(OrderRepository orderRepository, ExceptionHelper exceptionHelper) {
        this.orderRepository = orderRepository;
        this.exceptionHelper = exceptionHelper;
    }

    public BigDecimal calculateTotalAmount(Order order) {
        BigDecimal sum = Money.of(0.00);
        for (OrderedItem orderedItem : order.getOrderedItems()) {
            BigDecimal itemPrice = orderedItem.getMenuItemVariant().getPrice();
            int quantity = orderedItem.getQuantity();
            sum = sum.add(itemPrice.multiply(BigDecimal.valueOf(quantity)));
        }
        if (Objects.nonNull(order.getTipAmount())) {
            sum = sum.add(order.getTipAmount());
        }
        return sum.setScale(2, RoundingMode.HALF_UP);
    }

    public boolean orderExistsForGivenTable(Order order) throws LocalizedException {
        if (orderRepository.existsByRestaurantTable(order.getRestaurantTable())) {
            Integer tableNumber = order.getRestaurantTable().getId();
            Order existingOrder = orderRepository.findNewestOrderByTableNumber(tableNumber).orElseThrow();
            assertIsNotResolvedElseThrow(existingOrder);
        }
        return false;
    }

    public void prepareForFinalizingDineIn(Order existingOrder) {
        existingOrder.setPaid(true);
        existingOrder.setResolved(true);
    }

    public void prepareForFinalizingTakeAway(Order existingOrder) {
        existingOrder.setPaid(true);
        existingOrder.setResolved(true);
    }

    private void assertIsNotResolvedElseThrow(Order existingOrder) throws LocalizedException {
        if (!existingOrder.isResolved()) {
            exceptionHelper.throwLocalizedMessage("error.orderService.orderExistsForTable", existingOrder.getId());
        }
    }
}
