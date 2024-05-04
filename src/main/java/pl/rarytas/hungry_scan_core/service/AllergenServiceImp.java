package pl.rarytas.hungry_scan_core.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.rarytas.hungry_scan_core.entity.Allergen;
import pl.rarytas.hungry_scan_core.exception.ExceptionHelper;
import pl.rarytas.hungry_scan_core.exception.LocalizedException;
import pl.rarytas.hungry_scan_core.repository.AllergenRepository;
import pl.rarytas.hungry_scan_core.service.interfaces.AllergenService;

import java.util.List;

@Slf4j
@Service
public class AllergenServiceImp implements AllergenService {

    private final AllergenRepository allergenRepository;
    private final ExceptionHelper exceptionHelper;

    public AllergenServiceImp(AllergenRepository allergenRepository, ExceptionHelper exceptionHelper) {
        this.allergenRepository = allergenRepository;
        this.exceptionHelper = exceptionHelper;
    }

    @Override
    public void save(Allergen allergen) {
        allergenRepository.save(allergen);
    }

    @Override
    public List<Allergen> findAll() {
        return allergenRepository.findAll();
    }

    @Override
    public Allergen findById(Integer id) throws LocalizedException {
        return allergenRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.allergenService.allergenNotFound", id));
    }

    @Override
    public void delete(Integer id) throws LocalizedException {
        Allergen allergen = findById(id);
        allergenRepository.delete(allergen);
    }
}