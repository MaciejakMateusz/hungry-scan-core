package pl.rarytas.rarytas_restaurantside.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import pl.rarytas.rarytas_restaurantside.entity.Order;
import pl.rarytas.rarytas_restaurantside.entity.RestaurantTable;
import pl.rarytas.rarytas_restaurantside.enums.PaymentMethod;
import pl.rarytas.rarytas_restaurantside.exception.ExceptionHelper;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.repository.OrderRepository;
import pl.rarytas.rarytas_restaurantside.repository.RestaurantTableRepository;
import pl.rarytas.rarytas_restaurantside.service.interfaces.RestaurantTableService;
import pl.rarytas.rarytas_restaurantside.utility.Money;

import java.util.List;

@Slf4j
@Service
public class RestaurantTableServiceImp implements RestaurantTableService {

    private final RestaurantTableRepository restaurantTableRepository;
    private final OrderRepository orderRepository;
    private final ExceptionHelper exceptionHelper;
    private final SimpMessagingTemplate messagingTemplate;

    public RestaurantTableServiceImp(RestaurantTableRepository restaurantTableRepository, OrderRepository orderRepository,
                                     ExceptionHelper exceptionHelper, SimpMessagingTemplate messagingTemplate) {
        this.restaurantTableRepository = restaurantTableRepository;
        this.orderRepository = orderRepository;
        this.exceptionHelper = exceptionHelper;
        this.messagingTemplate = messagingTemplate;
    }


    @Override
    public List<RestaurantTable> findAll() {
        return restaurantTableRepository.findAll();
    }

    @Override
    public RestaurantTable findById(Integer id) throws LocalizedException {
        return restaurantTableRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.restaurantTableService.tableNotFound", id));
    }

    @Override
    public RestaurantTable findByToken(String token) throws LocalizedException {
        return restaurantTableRepository.findByToken(token)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.general.accessDenied"));
    }

    @Override
    public void save(RestaurantTable restaurantTable) {
        restaurantTableRepository.saveAndFlush(restaurantTable);
    }

    @Override
    public void toggleActivation(Integer id) throws LocalizedException {
        RestaurantTable table = findById(id);
        table.setActive(!table.isActive());
        save(table);
    }

    @Override
    public void callWaiter(Integer id) throws LocalizedException {
        RestaurantTable restaurantTable = findById(id);
        validateTableAction(restaurantTable);
        restaurantTable.setWaiterCalled(true);
        save(restaurantTable);
        messagingTemplate.convertAndSend("/topic/tables", findAll());
    }

    @Override
    public void resolveWaiterCall(Integer id) throws LocalizedException {
        RestaurantTable restaurantTable = findById(id);
        restaurantTable.setWaiterCalled(false);
        save(restaurantTable);
        messagingTemplate.convertAndSend("/topic/tables", findAll());
    }

    @Override
    public void requestBill(Integer id, PaymentMethod paymentMethod) throws LocalizedException {
        RestaurantTable restaurantTable = findById(id);
        validateTableAction(restaurantTable);
        notifyRelatedOrder(id, paymentMethod);
        restaurantTable.setBillRequested(true);
        save(restaurantTable);
        messagingTemplate.convertAndSend("/topic/tables", findAll());
    }

    private void notifyRelatedOrder(Integer tableNumber, PaymentMethod paymentMethod) throws LocalizedException {
        Order existingOrder = orderRepository.findNewestOrderByTableNumber(tableNumber)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.orderService.orderNotFoundByTable", tableNumber));

        if (Money.of(0.00).equals(existingOrder.getTotalAmount())) {
            exceptionHelper.throwLocalizedMessage("error.orderService.illegalBillRequest");
        }

        existingOrder.setPaymentMethod(paymentMethod);
        orderRepository.saveAndFlush(existingOrder);
        messagingTemplate.convertAndSend("/topic/dine-in-orders", findAll());
    }

    private void validateTableAction(RestaurantTable rt) throws LocalizedException {
        assertTableActivatedElseThrow(rt);
        assertWaiterNotCalledElseThrow(rt);
        assertBillNotRequestedElseThrow(rt);
    }

    private void assertTableActivatedElseThrow(RestaurantTable rt) throws LocalizedException {
        if (!rt.isActive()) {
            exceptionHelper.throwLocalizedMessage("error.restaurantTableService.tableNotActive", rt.getId());
        }
    }

    private void assertWaiterNotCalledElseThrow(RestaurantTable rt) throws LocalizedException {
        if (rt.isWaiterCalled()) {
            throwAlreadyRequested(rt);
        }
    }

    private void assertBillNotRequestedElseThrow(RestaurantTable rt) throws LocalizedException {
        if (rt.isBillRequested()) {
            throwAlreadyRequested(rt);
        }
    }

    private void throwAlreadyRequested(RestaurantTable rt) throws LocalizedException {
        exceptionHelper.throwLocalizedMessage("error.restaurantTableService.alreadyRequested", rt.getId());
    }
}