package pl.rarytas.rarytas_restaurantside.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.rarytas.rarytas_restaurantside.entity.Order;
import pl.rarytas.rarytas_restaurantside.entity.WaiterCall;
import pl.rarytas.rarytas_restaurantside.repository.WaiterCallRepository;
import pl.rarytas.rarytas_restaurantside.service.interfaces.WaiterCallService;

import java.util.Optional;

@Service
@Slf4j
public class WaiterCallServiceImpl implements WaiterCallService {

    private final WaiterCallRepository waiterCallRepository;

    public WaiterCallServiceImpl(WaiterCallRepository waiterCallRepository) {
        this.waiterCallRepository = waiterCallRepository;
    }

    @Override
    public void callWaiter(WaiterCall waiterCall) {
        waiterCallRepository.save(waiterCall);
    }

    @Override
    public Optional<WaiterCall> findByOrder(Order order) {
        return waiterCallRepository.findByOrder(order);
    }

    @Override
    public void delete(WaiterCall waiterCall) {
        waiterCallRepository.delete(waiterCall);
    }
}
