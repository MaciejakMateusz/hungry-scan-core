package pl.rarytas.rarytas_restaurantside.service;

import org.springframework.stereotype.Service;
import pl.rarytas.rarytas_restaurantside.entity.BillPart;
import pl.rarytas.rarytas_restaurantside.exception.ExceptionHelper;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.repository.BillPartRepository;
import pl.rarytas.rarytas_restaurantside.service.interfaces.BillPartService;

@Service
public class BillPartServiceImp implements BillPartService {

    private final BillPartRepository billPartRepository;
    private final ExceptionHelper exceptionHelper;

    public BillPartServiceImp(BillPartRepository billPartRepository, ExceptionHelper exceptionHelper) {
        this.billPartRepository = billPartRepository;
        this.exceptionHelper = exceptionHelper;
    }

    @Override
    public void save(BillPart billPart) {
        billPartRepository.save(billPart);
    }

    @Override
    public BillPart findById(Long id) throws LocalizedException {
        return billPartRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.billPartService.billPartNotFound", id));
    }

    @Override
    public void delete(Long id) throws LocalizedException {
        BillPart billPart = findById(id);
        billPartRepository.delete(billPart);
    }
}
