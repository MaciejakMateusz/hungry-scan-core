package pl.rarytas.rarytas_restaurantside.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import pl.rarytas.rarytas_restaurantside.entity.WaiterCall;
import pl.rarytas.rarytas_restaurantside.repository.WaiterCallRepository;
import pl.rarytas.rarytas_restaurantside.service.interfaces.WaiterCallServiceInterface;

@Service
@Slf4j
public class WaiterCallService implements WaiterCallServiceInterface {

    private final WaiterCallRepository waiterCallRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public WaiterCallService(WaiterCallRepository waiterCallRepository, SimpMessagingTemplate messagingTemplate) {
        this.waiterCallRepository = waiterCallRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void callWaiter(WaiterCall waiterCall) {
        waiterCallRepository.save(waiterCall);
        messagingTemplate.convertAndSend("/topic/waiter-call", waiterCall);
    }
}
