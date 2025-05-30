package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.entity.OrderSummary;
import com.hackybear.hungry_scan_core.entity.RestaurantTable;
import com.hackybear.hungry_scan_core.enums.PaymentMethod;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.OrderSummaryRepository;
import com.hackybear.hungry_scan_core.repository.RestaurantTableRepository;
import com.hackybear.hungry_scan_core.repository.UserRepository;
import com.hackybear.hungry_scan_core.service.interfaces.RestaurantTableService;
import com.hackybear.hungry_scan_core.utility.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RestaurantTableServiceImp implements RestaurantTableService {

    private final RestaurantTableRepository restaurantTableRepository;
    private final UserRepository userRepository;
    protected final OrderSummaryRepository orderSummaryRepository;
    protected final ExceptionHelper exceptionHelper;

    @Override
    public List<RestaurantTable> findAll() {
        return restaurantTableRepository.findAll();
    }

    @Override
    public RestaurantTable findById(Long id) throws LocalizedException {
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
    public void delete(Long id) throws LocalizedException {
        RestaurantTable existingTable = findById(id);
        assertTableNotActivatedElseThrow(existingTable);
        restaurantTableRepository.delete(existingTable);
    }

    @Override
    public RestaurantTable generateNewToken(Long id) throws LocalizedException {
        RestaurantTable existingTable = findById(id);
        assertTableNotActivatedElseThrow(existingTable);
        String token = String.valueOf(UUID.randomUUID());
        existingTable.setToken(token);
        save(existingTable);
        return existingTable;
    }

    @Override
    public void toggleActivation(Long id) throws LocalizedException {
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
    public void callWaiter(Long id) throws LocalizedException {
        RestaurantTable restaurantTable = findById(id);
        validateTableAction(restaurantTable);
        restaurantTable.setWaiterCalled(true);
        save(restaurantTable);
    }

    @Override
    public void resolveWaiterCall(Long id) throws LocalizedException {
        RestaurantTable restaurantTable = findById(id);
        restaurantTable.setWaiterCalled(false);
        save(restaurantTable);
    }

    @Override
    public void requestBill(Long id, PaymentMethod paymentMethod) throws LocalizedException {
        RestaurantTable restaurantTable = findById(id);
        validateTableAction(restaurantTable);
        notifyRelatedOrderSummary(id, paymentMethod);
        restaurantTable.setBillRequested(true);
        save(restaurantTable);
    }

    private void notifyRelatedOrderSummary(Long tableNumber, PaymentMethod paymentMethod) throws LocalizedException {
        OrderSummary existingSummary = orderSummaryRepository.findFirstByRestaurantTableId(tableNumber)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.orderSummaryService.summaryNotFound", tableNumber));

        if (Money.of(0.00).equals(existingSummary.getTotalAmount())) {
            exceptionHelper.throwLocalizedMessage("error.orderService.illegalBillRequest");
        }

        existingSummary.setPaymentMethod(paymentMethod);
        orderSummaryRepository.saveAndFlush(existingSummary);
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

    private OrderSummary getSummaryForTable(Long id) {
        return orderSummaryRepository.findFirstByRestaurantTableId(id)
                .orElse(new OrderSummary());
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

    private void assertTableNotActivatedElseThrow(RestaurantTable rt) throws LocalizedException {
        if (rt.isActive()) {
            exceptionHelper.throwLocalizedMessage("error.restaurantTableService.tableStillActive", rt.getId());
        }
    }

    private void assertWaiterNotCalledElseThrow(RestaurantTable rt) throws LocalizedException {
        if (rt.isWaiterCalled()) {
            exceptionHelper.throwLocalizedMessage("error.restaurantTableService.alreadyRequested");
        }
    }

    private void assertBillNotRequestedElseThrow(RestaurantTable rt) throws LocalizedException {
        if (rt.isBillRequested()) {
            exceptionHelper.throwLocalizedMessage("error.restaurantTableService.alreadyRequested");
        }
    }

    private void validateTableCreation(RestaurantTable restaurantTable) throws LocalizedException {
        Integer tableNumber = restaurantTable.getNumber();
        if (restaurantTableRepository.existsByNumber(tableNumber)) {
            exceptionHelper.throwLocalizedMessage("error.restaurantTableService.tableNumberExists", tableNumber);
        }
    }

}