package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.dto.AllergenDTO;
import com.hackybear.hungry_scan_core.dto.mapper.AllergenMapper;
import com.hackybear.hungry_scan_core.entity.Allergen;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.AllergenRepository;
import com.hackybear.hungry_scan_core.service.interfaces.AllergenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AllergenServiceImp implements AllergenService {

    private final AllergenRepository allergenRepository;
    private final ExceptionHelper exceptionHelper;
    private final AllergenMapper allergenMapper;

    public AllergenServiceImp(AllergenRepository allergenRepository,
                              ExceptionHelper exceptionHelper,
                              AllergenMapper allergenMapper) {
        this.allergenRepository = allergenRepository;
        this.exceptionHelper = exceptionHelper;
        this.allergenMapper = allergenMapper;
    }

    @Override
    public void save(AllergenDTO allergenDTO) {
        Allergen allergen = allergenMapper.toAllergen(allergenDTO);
        allergenRepository.save(allergen);
    }

    @Override
    public List<AllergenDTO> findAll() {
        List<Allergen> allergens = allergenRepository.findAll();
        return allergens.stream().map(allergenMapper::toDTO).toList();
    }

    @Override
    public AllergenDTO findById(Long id) throws LocalizedException {
        Allergen allergen = getAllergen(id);
        return allergenMapper.toDTO(allergen);
    }

    @Override
    public void delete(Long id) throws LocalizedException {
        allergenRepository.deleteById(id);
    }

    private Allergen getAllergen(Long id) throws LocalizedException {
        return allergenRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.allergenService.allergenNotFound", id));
    }
}