package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.dto.LabelDTO;
import com.hackybear.hungry_scan_core.dto.mapper.LabelMapper;
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
    private final LabelMapper labelMapper;

    public LabelServiceImp(LabelRepository labelRepository, ExceptionHelper exceptionHelper, LabelMapper labelMapper) {
        this.labelRepository = labelRepository;
        this.exceptionHelper = exceptionHelper;
        this.labelMapper = labelMapper;
    }

    @Override
    public void save(LabelDTO labelDTO) {
        Label label = labelMapper.toLabel(labelDTO);
        labelRepository.save(label);
    }

    @Override
    public List<LabelDTO> findAll() {
        List<Label> labels = labelRepository.findAll();
        return labels.stream().map(labelMapper::toDTO).toList();
    }

    @Override
    public LabelDTO findById(Long id) throws LocalizedException {
        Label label = getLabel(id);
        return labelMapper.toDTO(label);
    }

    @Override
    public void delete(Long id) throws LocalizedException {
        labelRepository.deleteById(id);
    }

    private Label getLabel(Long id) throws LocalizedException {
        return labelRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.labelService.labelNotFound", id));
    }
}