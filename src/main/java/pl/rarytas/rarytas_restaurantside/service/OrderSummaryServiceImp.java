package pl.rarytas.rarytas_restaurantside.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.rarytas.rarytas_restaurantside.entity.OrderSummary;
import pl.rarytas.rarytas_restaurantside.exception.ExceptionHelper;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.repository.OrderSummaryRepository;
import pl.rarytas.rarytas_restaurantside.service.interfaces.OrderSummaryService;
import pl.rarytas.rarytas_restaurantside.utility.OrderSummaryServiceHelper;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
public class OrderSummaryServiceImp implements OrderSummaryService {

    private final OrderSummaryRepository orderSummaryRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ArchiveDataServiceImp dataTransferServiceImpl;
    private final OrderSummaryServiceHelper summaryHelper;
    private final ExceptionHelper exceptionHelper;

    public OrderSummaryServiceImp(OrderSummaryRepository orderSummaryRepository,
                                  SimpMessagingTemplate messagingTemplate,
                                  ArchiveDataServiceImp dataTransferServiceImpl,
                                  OrderSummaryServiceHelper summaryHelper,
                                  ExceptionHelper exceptionHelper) {
        this.orderSummaryRepository = orderSummaryRepository;
        this.messagingTemplate = messagingTemplate;
        this.dataTransferServiceImpl = dataTransferServiceImpl;
        this.summaryHelper = summaryHelper;
        this.exceptionHelper = exceptionHelper;
    }

    @Override
    public List<OrderSummary> findAll() {
        return orderSummaryRepository.findAll();
    }

    @Override
    public OrderSummary findById(Long id) throws LocalizedException {
        return orderSummaryRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.orderService.orderNotFound", id));
    }

    @Override
    public OrderSummary findByTableNumber(Integer tableNumber) throws LocalizedException {
        return orderSummaryRepository.findFirstByRestaurantTableId(tableNumber)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.orderService.orderNotFoundByTable", tableNumber));
    }

    @Override
    @Transactional
    public void tip(Long id, BigDecimal tipAmount) throws LocalizedException {
        if (tipAmount.compareTo(BigDecimal.ZERO) <= 0) {
            exceptionHelper.throwLocalizedMessage("error.orderService.invalidTipAmount");
        }
        OrderSummary existingSummary = findById(id);
        existingSummary.setTipAmount(tipAmount);
        saveRefreshAndNotify(existingSummary);
    }

    @Override
    public void finish(Long id) throws LocalizedException {
        OrderSummary existingSummary = findById(id);
        orderSummaryRepository.saveAndFlush(existingSummary);
        dataTransferServiceImpl.archiveSummary(existingSummary);
    }

    @Override
    public void delete(OrderSummary orderSummary) {
        orderSummaryRepository.delete(orderSummary);
    }

    private void saveRefreshAndNotify(OrderSummary orderSummary) {
        orderSummary.setTotalAmount(summaryHelper.calculateTotalPrice(orderSummary));
        orderSummaryRepository.saveAndFlush(orderSummary);
        messagingTemplate.convertAndSend("/topic/summaries", findAll());
    }
}