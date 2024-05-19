package com.hackybear.hungry_scan_core.controller.restaurant;

import com.hackybear.hungry_scan_core.controller.ResponseHelper;
import com.hackybear.hungry_scan_core.entity.Feedback;
import com.hackybear.hungry_scan_core.service.interfaces.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/restaurant/feedback")
@PreAuthorize("isAuthenticated()")
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final ResponseHelper responseHelper;

    public FeedbackController(FeedbackService feedbackService, ResponseHelper responseHelper) {
        this.feedbackService = feedbackService;
        this.responseHelper = responseHelper;
    }

    @PostMapping("/show")
    public ResponseEntity<Map<String, Object>> show(@RequestBody Integer id) {
        return responseHelper.getResponseEntity(id, feedbackService::findById);
    }

    @PostMapping("/add")
    public ResponseEntity<?> save(@RequestBody @Valid Feedback feedback, BindingResult br) {
        return responseHelper.buildResponse(feedback, br, feedbackService::save);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> getByDate(@RequestBody Pageable pageable) {
        return responseHelper.buildResponse(pageable, feedbackService::findAll);
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, POST, OPTIONS");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}