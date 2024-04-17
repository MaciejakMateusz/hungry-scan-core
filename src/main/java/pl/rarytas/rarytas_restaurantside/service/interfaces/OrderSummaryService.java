package pl.rarytas.rarytas_restaurantside.service.interfaces;

import pl.rarytas.rarytas_restaurantside.entity.OrderSummary;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;

import java.math.BigDecimal;
import java.util.List;

public interface OrderSummaryService {

    List<OrderSummary> findAll();

    OrderSummary findById(Long id) throws LocalizedException;

    OrderSummary findByTableNumber(Integer tableNumber) throws LocalizedException;

    void tip(Long id, BigDecimal tipAmount) throws LocalizedException;

    void finish(Long id) throws LocalizedException;

    void delete(OrderSummary orderSummary);
}
