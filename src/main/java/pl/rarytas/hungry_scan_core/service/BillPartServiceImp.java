package pl.rarytas.hungry_scan_core.service;

import org.springframework.stereotype.Service;
import pl.rarytas.hungry_scan_core.entity.BillPart;
import pl.rarytas.hungry_scan_core.exception.ExceptionHelper;
import pl.rarytas.hungry_scan_core.exception.LocalizedException;
import pl.rarytas.hungry_scan_core.repository.BillPartRepository;
import pl.rarytas.hungry_scan_core.service.interfaces.BillPartService;

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
