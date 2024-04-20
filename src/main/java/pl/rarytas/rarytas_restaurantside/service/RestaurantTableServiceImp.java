package pl.rarytas.rarytas_restaurantside.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import pl.rarytas.rarytas_restaurantside.entity.OrderSummary;
import pl.rarytas.rarytas_restaurantside.entity.RestaurantTable;
import pl.rarytas.rarytas_restaurantside.entity.Zone;
import pl.rarytas.rarytas_restaurantside.enums.PaymentMethod;
import pl.rarytas.rarytas_restaurantside.exception.ExceptionHelper;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.repository.OrderSummaryRepository;
import pl.rarytas.rarytas_restaurantside.repository.RestaurantTableRepository;
import pl.rarytas.rarytas_restaurantside.repository.UserRepository;
import pl.rarytas.rarytas_restaurantside.repository.ZoneRepository;
import pl.rarytas.rarytas_restaurantside.service.interfaces.RestaurantTableService;
import pl.rarytas.rarytas_restaurantside.utility.Money;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class RestaurantTableServiceImp implements RestaurantTableService {

    private final RestaurantTableRepository restaurantTableRepository;
    private final UserRepository userRepository;
    private final ZoneRepository zoneRepository;
    protected final OrderSummaryRepository orderSummaryRepository;
    protected final ExceptionHelper exceptionHelper;
    protected final SimpMessagingTemplate messagingTemplate;

    public RestaurantTableServiceImp(RestaurantTableRepository restaurantTableRepository,
                                     UserRepository userRepository, ZoneRepository zoneRepository,
                                     OrderSummaryRepository orderSummaryRepository,
                                     ExceptionHelper exceptionHelper,
                                     SimpMessagingTemplate messagingTemplate) {
        this.restaurantTableRepository = restaurantTableRepository;
        this.userRepository = userRepository;
        this.zoneRepository = zoneRepository;
        this.orderSummaryRepository = orderSummaryRepository;
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
    public RestaurantTable findByNumber(Integer number) throws LocalizedException {
        return restaurantTableRepository.findByNumber(number)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.restaurantTableService.tableWithNumberNotFound", number));
    }

    @Override
    public RestaurantTable findByToken(String token) throws LocalizedException {
        return restaurantTableRepository.findByToken(token)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.general.accessDenied"));
    }

    @Override
    public void createNew(RestaurantTable restaurantTable) throws LocalizedException {
        validateTableCreation(restaurantTable);
        restaurantTableRepository.save(restaurantTable);
    }

    @Override
    public void save(RestaurantTable restaurantTable) {
        restaurantTableRepository.save(restaurantTable);
    }

    @Override
    public void delete(Integer id) throws LocalizedException {
        RestaurantTable restaurantTable = findById(id);
        restaurantTableRepository.delete(restaurantTable);
    }

    @Override
    public void generateNewToken(Integer id) throws LocalizedException {
        RestaurantTable existingTable = findById(id);
        String token = String.valueOf(UUID.randomUUID());
        existingTable.setToken(token);
        save(existingTable);
    }

    @Override
    public void changeZone(Integer tableId, Integer zoneId) throws LocalizedException {
        RestaurantTable table = findById(tableId);
        Zone zone = getZoneByRestaurantTable(table);
        zone.removeRestaurantTable(table);
        zoneRepository.save(zone);
        Zone newZone = getZoneById(zoneId);
        newZone.addRestaurantTable(table);
        zoneRepository.save(newZone);
    }

    @Override
    public void toggleActivation(Integer id) throws LocalizedException {
        RestaurantTable table = findById(id);
        if (isToggleValid(table)) {
            removeUsersAccess(table);
            table.setActive(!table.isActive());
            save(table);
        } else {
            exceptionHelper.throwLocalizedMessage("error.restaurantTableService.tableNotPaid", id);
        }
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
        notifyRelatedOrderSummary(id, paymentMethod);
        restaurantTable.setBillRequested(true);
        save(restaurantTable);
        messagingTemplate.convertAndSend("/topic/tables", findAll());
    }

    private Zone getZoneByRestaurantTable(RestaurantTable table) throws LocalizedException {
        return zoneRepository.findByRestaurantTable(table)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.restaurantTableService.zoneNotFound", table.getId()));
    }

    private Zone getZoneById(Integer id) throws LocalizedException {
        return zoneRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.zoneService.zoneNotFound", id));
    }

    private void notifyRelatedOrderSummary(Integer tableNumber, PaymentMethod paymentMethod) throws LocalizedException {
        OrderSummary existingSummary = orderSummaryRepository.findFirstByRestaurantTableId(tableNumber)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.orderSummaryService.summaryNotFound", tableNumber));

        if (Money.of(0.00).equals(existingSummary.getTotalAmount())) {
            exceptionHelper.throwLocalizedMessage("error.orderService.illegalBillRequest");
        }

        existingSummary.setPaymentMethod(paymentMethod);
        orderSummaryRepository.saveAndFlush(existingSummary);
        messagingTemplate.convertAndSend("/topic/summaries", findAll());
    }

    private void removeUsersAccess(RestaurantTable table) {
        userRepository.deleteAll(table.getUsers());
        table.setUsers(new HashSet<>());
    }

    private boolean isToggleValid(RestaurantTable table) {
        if (table.isActive()) {
            OrderSummary orderSummary = getSummaryForTable(table.getId());
            if (orderSummary.getOrders().isEmpty()) {
                return true;
            }
            return orderSummary.isPaid();
        }
        return true;
    }

    private OrderSummary getSummaryForTable(Integer id) {
        return orderSummaryRepository.findFirstByRestaurantTableId(id)
                .orElse(null);
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

    private void validateTableCreation(RestaurantTable restaurantTable) throws LocalizedException {
        Integer tableNumber = restaurantTable.getNumber();
        if (restaurantTableRepository.existsByNumber(tableNumber)) {
            exceptionHelper.throwLocalizedMessage("error.restaurantTableService.tableNumberExists", tableNumber);
        }
    }

    private void throwAlreadyRequested(RestaurantTable rt) throws LocalizedException {
        exceptionHelper.throwLocalizedMessage("error.restaurantTableService.alreadyRequested", rt.getId());
    }

}