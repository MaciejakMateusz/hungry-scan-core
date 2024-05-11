package com.hackybear.hungry_scan_core.controller;

import com.hackybear.hungry_scan_core.utility.interfaces.ThrowingBiConsumer;
import com.hackybear.hungry_scan_core.utility.interfaces.ThrowingConsumer;
import com.hackybear.hungry_scan_core.utility.interfaces.ThrowingFunction;
import com.hackybear.hungry_scan_core.utility.interfaces.TriFunction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;


/**
 * This component provides support methods for building response entities.
 */
@Slf4j
@Component
public class ResponseHelper {

    /**
     * Creates ResponseEntity based on provided parameters.
     * If BindingResult has no errors it persists passed entity in a database.
     *
     * @param entity       Entity to persist in database.
     * @param br           BindingResult created by using @Valid next to entity object in method parameters.
     *                     For example (@Valid User entity, BindingResult br) - br will hold errors if they occurred.
     * @param saveFunction Behaviour to pass from a given service. For example userService::save
     * @return ResponseEntity with appropriate response code and body containing parameters map.
     */
    public <ENTITY> ResponseEntity<?> buildResponse(ENTITY entity,
                                                    BindingResult br,
                                                    ThrowingConsumer<ENTITY> saveFunction) {
        return br.hasErrors() ? createErrorResponse(br) : acceptAndCreateSuccessResponse(saveFunction, entity);
    }

    /**
     * Creates ResponseEntity based on provided parameters.
     *
     * @param t        Object to accept by the consumer.
     * @param consumer Behaviour to pass from a given service. For example bookingService::delete
     * @return ResponseEntity with appropriate response code and body containing parameters map.
     */
    public <T> ResponseEntity<Map<String, Object>> buildResponse(T t, ThrowingConsumer<T> consumer) {
        try {
            consumer.accept(t);
        } catch (Exception e) {
            return createErrorResponse(e);
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Creates ResponseEntity based on provided parameters.
     *
     * @param t        Object to accept by the consumer.
     * @param r        Object to accept by the consumer.
     * @param consumer Behaviour to pass from a given service. For example bookingService::delete
     * @return ResponseEntity with appropriate response code and body containing parameters map.
     */
    public <T, R> ResponseEntity<Map<String, Object>> buildResponse(T t, R r, ThrowingBiConsumer<T, R> consumer) {
        try {
            consumer.accept(t, r);
        } catch (Exception e) {
            return createErrorResponse(e);
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Finds entity in database based on provided ID.
     * Creates ResponseEntity based on provided parameters.
     *
     * @param id       ID of entity to get from a database.
     * @param function Behaviour to pass from a given service. For example userService::findById
     * @return ResponseEntity with appropriate response code and body containing parameters map.
     */
    public <ID, ENTITY> ResponseEntity<Map<String, Object>> getResponseEntity(ID id,
                                                                              ThrowingFunction<ID, ENTITY> function) {
        ENTITY entity;
        try {
            entity = function.apply(id);
        } catch (Exception e) {
            return createErrorResponse(e);
        }
        return createSuccessResponse(entity);
    }

    /**
     * Creates ResponseEntity based on provided parameters.
     *
     * @param id       ID of entity that exists in database.
     * @param function Behaviour to pass from a given service. For example orderService::finishDineIn
     * @return ResponseEntity with appropriate response code and body containing parameters map.
     */
    public <ID> ResponseEntity<Map<String, Object>> getResponseEntity(ID id, ThrowingConsumer<ID> function) {
        try {
            function.accept(id);
        } catch (Exception e) {
            return createErrorResponse(e);
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Gets errors based on provided BindingResult.
     * Should be used after check br.hasErrors()
     *
     * @param br BindingResult passed from controller's argument.
     * @return HashMap of field errors that occurred while validating given entity.
     */

    public Map<String, String> getFieldErrors(BindingResult br) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : br.getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }
        return fieldErrors;
    }

    /**
     * Retrieves a paginated collection of entities filtered by date range.
     *
     * @param requestBody Map containing request parameters.
     * @param getByDate   Function to fetch entities by date range.
     * @param <T>         Type of entities.
     * @return ResponseEntity containing the paginated collection of entities.
     */
    public <T> ResponseEntity<Page<T>> getEntitiesByDateRange(
            Map<String, Object> requestBody,
            TriFunction<Pageable, LocalDate, LocalDate, Page<T>> getByDate) {
        Integer pageNumber = (Integer) requestBody.get("pageNumber");
        Integer pageSize = (Integer) requestBody.get("pageSize");
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        LocalDate startDate = LocalDate.parse((CharSequence) requestBody.get("dateFrom"));
        LocalDate endDate = LocalDate.parse((CharSequence) requestBody.get("dateTo"));
        Page<T> entities = getByDate.apply(pageable, startDate, endDate);
        return ResponseEntity.ok(entities);
    }

    private ResponseEntity<?> createErrorResponse(BindingResult br) {
        return ResponseEntity.badRequest().body(getFieldErrors(br));
    }

    public ResponseEntity<Map<String, Object>> createErrorResponse(Exception e) {
        log.error(e.getMessage());
        Map<String, Object> params = Map.of("exceptionMsg", e.getMessage());
        return ResponseEntity.badRequest().body(params);
    }

    private <ENTITY> ResponseEntity<Map<String, Object>> createSuccessResponse(ENTITY entity) {
        String simpleClassName = entity.getClass().getSimpleName();
        String paramName = Character.toLowerCase(simpleClassName.charAt(0)) + simpleClassName.substring(1);
        Map<String, Object> params = Map.of(paramName, entity);
        return ResponseEntity.ok(params);
    }

    private <ENTITY> ResponseEntity<Map<String, Object>> acceptAndCreateSuccessResponse(ThrowingConsumer<ENTITY> saveFunction,
                                                                                        ENTITY entity) {
        try {
            saveFunction.accept(entity);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("exceptionMsg", e.getMessage()));
        }
        return ResponseEntity.ok().body(new HashMap<>());
    }
}