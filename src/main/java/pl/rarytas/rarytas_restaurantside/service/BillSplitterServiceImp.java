package pl.rarytas.rarytas_restaurantside.service;

import org.springframework.stereotype.Service;
import pl.rarytas.rarytas_restaurantside.entity.BillSplitter;
import pl.rarytas.rarytas_restaurantside.exception.ExceptionHelper;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.repository.BillSplitterRepository;
import pl.rarytas.rarytas_restaurantside.service.interfaces.BillSplitterService;

@Service
public class BillSplitterServiceImp implements BillSplitterService {

    private final BillSplitterRepository billSplitterRepository;
    private final ExceptionHelper exceptionHelper;

    public BillSplitterServiceImp(BillSplitterRepository billSplitterRepository, ExceptionHelper exceptionHelper) {
        this.billSplitterRepository = billSplitterRepository;
        this.exceptionHelper = exceptionHelper;
    }

    @Override
    public void splitBill(BillSplitter billSplitter) {
        billSplitterRepository.save(billSplitter);
    }

    @Override
    public BillSplitter findById(Long id) throws LocalizedException {
        return billSplitterRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.billSplitterService.splitterNotFound", id));
    }

    @Override
    public void delete(Long id) throws LocalizedException {
        BillSplitter billSplitter = findById(id);
        billSplitterRepository.delete(billSplitter);
    }
}
