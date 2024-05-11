package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.entity.Label;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.LabelRepository;
import com.hackybear.hungry_scan_core.service.interfaces.LabelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class LabelServiceImp implements LabelService {

    private final LabelRepository labelRepository;
    private final ExceptionHelper exceptionHelper;

    public LabelServiceImp(LabelRepository labelRepository, ExceptionHelper exceptionHelper) {
        this.labelRepository = labelRepository;
        this.exceptionHelper = exceptionHelper;
    }

    @Override
    public void save(Label label) {
        labelRepository.save(label);
    }

    @Override
    public List<Label> findAll() {
        return labelRepository.findAll();
    }

    @Override
    public Label findById(Integer id) throws LocalizedException {
        return labelRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.labelService.labelNotFound", id));
    }

    @Override
    public void delete(Integer id) throws LocalizedException {
        Label label = findById(id);
        labelRepository.delete(label);
    }
}