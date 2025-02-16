package com.hackybear.hungry_scan_core.controller;

import com.hackybear.hungry_scan_core.dto.RegistrationDTO;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.interfaces.*;
import com.hackybear.hungry_scan_core.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.function.ThrowingSupplier;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.net.URI;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


/**
 * This component provides support methods for building response entities.
 */
@Component
public class ResponseHelper {

    private final ExceptionHelper exceptionHelper;
    private final UserRepository userRepository;

    @Value("${CMS_APP_URL}")
    private String cmsAppUrl;

    public ResponseHelper(ExceptionHelper exceptionHelper,
                          UserRepository userRepository) {
        this.exceptionHelper = exceptionHelper;
        this.userRepository = userRepository;
    }

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
     * Creates ResponseEntity based on provided parameters.
     *
     * @param t        Object to accept by the consumer.
     * @param br       BindingResult to check fields validation.
     * @param supplier Supplier to provide needed parameter.
     * @param consumer Behaviour to pass from a given service. For example bookingService::delete
     * @return ResponseEntity with appropriate response code and body containing parameters map.
     */
    public <T, R> ResponseEntity<?> buildResponse(T t,
                                                  BindingResult br,
                                                  ThrowingSupplier<R> supplier,
                                                  ThrowingBiConsumer<T, R> consumer) {
        try {
            if (br.hasErrors()) {
                return createErrorResponse(br);
            }
            R r = supplier.get();
            consumer.accept(t, r);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return createErrorResponse(e);
        }
    }

    /**
     * Creates ResponseEntity based on provided parameters.
     *
     * @param t        Object to accept by the consumer.
     * @param supplier Supplier to provide needed parameter.
     * @param consumer Behaviour to pass from a given service. For example bookingService::delete
     * @return ResponseEntity with appropriate response code and body containing parameters map.
     */
    public <T, R> ResponseEntity<?> buildResponse(T t,
                                                  ThrowingSupplier<R> supplier,
                                                  ThrowingBiConsumer<T, R> consumer) {
        try {
            R r = supplier.get();
            consumer.accept(t, r);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return createErrorResponse(e);
        }
    }

    /**
     * Creates ResponseEntity based on provided parameters.
     *
     * @param t          Object to accept by the consumer.
     * @param supplier   Supplier to provide needed parameter.
     * @param biSupplier Behaviour to pass from a given service. For example bookingService::delete
     * @return ResponseEntity with appropriate response code and body containing parameters map.
     */
    public <T, R, U> ResponseEntity<?> buildResponse(T t,
                                                     ThrowingSupplier<R> supplier,
                                                     ThrowingBiSupplier<T, R, U> biSupplier) {
        try {
            R r = supplier.get();
            return createSuccessResponse(biSupplier.get(t, r));
        } catch (Exception e) {
            return createErrorResponse(e);
        }
    }

    /**
     * Creates ResponseEntity based on provided parameters.
     *
     * @param supplier Supplier to provide needed parameter.
     * @param function Behaviour to pass from a given service. For example bookingService::findAll
     * @return ResponseEntity with appropriate response code and body containing parameters map.
     */
    public <T> ResponseEntity<?> buildResponse(ThrowingSupplier<T> supplier, Function<T, ?> function) {
        try {
            T t = supplier.get();
            return ResponseEntity.ok(function.apply(t));
        } catch (Exception e) {
            return createErrorResponse(e);
        }
    }

    /**
     * Creates ResponseEntity based on provided parameters.
     *
     * @param t        Object to apply to the function.
     * @param function Behaviour to pass from a given service. For example orderService::findByTable
     * @return ResponseEntity with appropriate response code and body containing object or map with exception.
     */
    public <T, R> ResponseEntity<?> getObjectAndBuildResponse(T t, ThrowingFunction<T, R> function) {
        try {
            R r = function.apply(t);
            return ResponseEntity.ok(r);
        } catch (Exception e) {
            return createErrorResponse(e);
        }
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

    public ResponseEntity<Map<String, Object>> createErrorResponse(Exception e) {
        Map<String, Object> params = Map.of("exceptionMsg", e.getMessage());
        return ResponseEntity.badRequest().body(params);
    }

    public Map<String, Object> getErrorParams(RegistrationDTO registrationDTO) {
        Map<String, Object> params = new HashMap<>();
        if (userRepository.existsByUsername(registrationDTO.username())) {
            params.put("givenUsername", registrationDTO.username());
            params.put("username", exceptionHelper.getLocalizedMsg("validation.username.usernameExists"));
        } else if (!registrationDTO.password().equals(registrationDTO.repeatedPassword())) {
            params.put("repeatedPassword", exceptionHelper.getLocalizedMsg("validation.repeatedPassword.notMatch"));
        }
        return params;
    }

    public ResponseEntity<?> redirectTo(String url) {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(cmsAppUrl + url))
                .build();
    }

    private ResponseEntity<?> createErrorResponse(BindingResult br) {
        return ResponseEntity.badRequest().body(getFieldErrors(br));
    }

    private <ENTITY> ResponseEntity<Map<String, Object>> createSuccessResponse(ENTITY entity) {
        String simpleClassName = entity.getClass().getSimpleName();
        String paramName = Character.toLowerCase(simpleClassName.charAt(0)) + simpleClassName.substring(1);
        Map<String, Object> params = Map.of(paramName, entity);
        return ResponseEntity.ok(params);
    }

    private <ENTITY> ResponseEntity<Map<String, Object>> acceptAndCreateSuccessResponse(ThrowingConsumer<ENTITY> saveFunction, ENTITY entity) {
        try {
            saveFunction.accept(entity);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("exceptionMsg", e.getMessage()));
        }
        return ResponseEntity.ok().body(new HashMap<>());
    }
}