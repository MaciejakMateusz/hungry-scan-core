package pl.rarytas.rarytas_restaurantside.utility;

import org.springframework.stereotype.Component;
import pl.rarytas.rarytas_restaurantside.entity.Order;
import pl.rarytas.rarytas_restaurantside.entity.OrderedItem;
import pl.rarytas.rarytas_restaurantside.exception.ExceptionHelper;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.repository.OrderRepository;

import java.math.BigDecimal;

@Component
public class OrderServiceHelper {

    private final OrderRepository orderRepository;
    private final ExceptionHelper exceptionHelper;

    public OrderServiceHelper(OrderRepository orderRepository, ExceptionHelper exceptionHelper) {
        this.orderRepository = orderRepository;
        this.exceptionHelper = exceptionHelper;
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

    public void prepareForFinalizingDineIn(Order existingOrder) {
        existingOrder.setPaid(true);
        existingOrder.setResolved(true);
        existingOrder.setTotalAmount(calculateTotalAmount(existingOrder));
        existingOrder.setBillRequested(true);
    }

    public void prepareForFinalizingTakeAway(Order existingOrder) {
        existingOrder.setPaid(true);
        existingOrder.setResolved(true);
        existingOrder.setTotalAmount(calculateTotalAmount(existingOrder));
    }

    public void assertOrderExistsElseThrow(Long id) throws LocalizedException {
        if (!orderRepository.existsById(id)) {
            exceptionHelper.throwLocalizedMessage("error.orderService.general.orderNotfound", id);
        }
    }

    private void assertIsNotResolvedElseThrow(Order existingOrder) throws LocalizedException {
        if (!existingOrder.isResolved()) {
            exceptionHelper.throwLocalizedMessage("error.orderService.general.orderExistsForTable", existingOrder.getId());
        }
    }

    public void assertWaiterNotCalledElseThrow(Order existingOrder) throws LocalizedException {
        if (existingOrder.isWaiterCalled()) {
            throwAlreadyRequested(existingOrder);
        }
    }

    public void assertBillNotRequestedElseThrow(Order existingOrder) throws LocalizedException {
        if (existingOrder.isBillRequested()) {
            throwAlreadyRequested(existingOrder);
        }
    }

    private void throwAlreadyRequested(Order existingOrder) throws LocalizedException {
        exceptionHelper.throwLocalizedMessage("error.orderService.general.alreadyRequested", existingOrder.getId());
    }
}
