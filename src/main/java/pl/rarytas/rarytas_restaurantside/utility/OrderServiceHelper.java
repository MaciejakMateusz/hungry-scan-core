package pl.rarytas.rarytas_restaurantside.utility;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import pl.rarytas.rarytas_restaurantside.entity.Order;
import pl.rarytas.rarytas_restaurantside.entity.OrderedItem;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.repository.OrderRepository;

import java.math.BigDecimal;

@Component
public class OrderServiceHelper {

    private final OrderRepository orderRepository;
    private final MessageSource messageSource;

    public OrderServiceHelper(OrderRepository orderRepository, MessageSource messageSource) {
        this.orderRepository = orderRepository;
        this.messageSource = messageSource;
    }

    private BigDecimal calculateTotalAmount(Order order) {
        BigDecimal sum = BigDecimal.valueOf(0);
        for (OrderedItem orderedItem : order.getOrderedItems()) {
            BigDecimal itemPrice = orderedItem.getMenuItem().getPrice();
            int quantity = orderedItem.getQuantity();
            sum = sum.add(itemPrice.multiply(BigDecimal.valueOf(quantity)));
        }
        return sum;
    }

    public boolean orderExistsForGivenTable(Order order) throws LocalizedException {
        if (orderRepository.existsByRestaurantTable(order.getRestaurantTable())) {
            Integer tableNumber = order.getRestaurantTable().getId();
            Order existingOrder = orderRepository.findNewestOrderByTableNumber(tableNumber).orElseThrow();
            assertIsNotResolvedElseThrow(existingOrder);
        }
        return false;
    }

    public void prepareForFinalizing(Order existingOrder, boolean paid, boolean isResolved) {
        existingOrder.setPaid(paid);
        existingOrder.setResolved(isResolved);
        existingOrder.setTotalAmount(calculateTotalAmount(existingOrder));
        assert !existingOrder.isForTakeAway();
        existingOrder.setBillRequested(true);
    }

    public void assertOrderExistsElseThrow(Integer id) throws LocalizedException {
        if (!orderRepository.existsById(id)) {
            throw new LocalizedException(String.format(messageSource.getMessage(
                    "error.orderService.general.orderNotfound",
                    new Integer[]{id}, LocaleContextHolder.getLocale())));
        }
    }

    private void assertIsNotResolvedElseThrow(Order existingOrder) throws LocalizedException {
        if (!existingOrder.isResolved()) {
            throw new LocalizedException(String.format(messageSource.getMessage(
                    "error.orderService.general.orderExistsForTable",
                    new Integer[]{existingOrder.getId()}, LocaleContextHolder.getLocale())));
        }
    }

    public void assertWaiterNotCalledElseThrow(Order existingOrder) throws LocalizedException {
        if (existingOrder.isWaiterCalled()) {
            throwAlreadyRequested(existingOrder);
        }
    }

    public void assertNoBillRequestedElseThrow(Order existingOrder) throws LocalizedException {
        if (existingOrder.isBillRequested()) {
            throwAlreadyRequested(existingOrder);
        }
    }

    private void throwAlreadyRequested(Order existingOrder) throws LocalizedException {
        throw new LocalizedException(String.format(messageSource.getMessage(
                "error.orderService.general.alreadyRequested",
                new Integer[]{existingOrder.getId()}, LocaleContextHolder.getLocale())));
    }
}
