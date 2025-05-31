package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.entity.Feedback;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.FeedbackRepository;
import com.hackybear.hungry_scan_core.service.interfaces.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImp implements FeedbackService {


    private final FeedbackRepository feedbackRepository;
    private final ExceptionHelper exceptionHelper;

    @Override
    public Feedback findById(Long id) throws LocalizedException {
        return feedbackRepository.findById(id)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.feedbackService.feedbackNotFound", id));
    }

    @Override
    public Page<Feedback> findAll(Pageable pageable) {
        return feedbackRepository.findAll(pageable);
    }

    @Override
    public void save(Feedback feedback) {
        feedbackRepository.save(feedback);
    }

}