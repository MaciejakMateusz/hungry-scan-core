package com.hackybear.hungry_scan_core.controller;

import com.hackybear.hungry_scan_core.dto.RegistrationDTO;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.interfaces.*;
import com.hackybear.hungry_scan_core.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
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
 * This component provides helper methods to build standardized ResponseEntity objects
 * for various scenarios (e.g., saving entities, retrieving data, handling errors, and redirects).
 * It encapsulates common patterns of exception handling and result packaging to streamline
 * controller code.
 */
@Component
@Slf4j
public class ResponseHelper {

    private final ExceptionHelper exceptionHelper;
    private final UserRepository userRepository;

    /**
     * Base URL for redirection responses. This value is injected from the property "CMS_APP_URL".
     */
    @Value("${CMS_APP_URL}")
    private String cmsAppUrl;

    public ResponseHelper(ExceptionHelper exceptionHelper,
                          UserRepository userRepository) {
        this.exceptionHelper = exceptionHelper;
        this.userRepository = userRepository;
    }

    /**
     * Creates a ResponseEntity based on the provided entity and BindingResult.
     * <p>
     * If the BindingResult contains errors, a bad request response is returned with a map of field errors.
     * Otherwise, the given save function is executed to persist the entity, and an OK response is returned.
     *
     * @param entity       the entity to persist in the database
     * @param br           the BindingResult containing validation errors (if any)
     * @param saveFunction the function to persist the entity (e.g., userService::save)
     * @param <ENTITY>     the type of the entity
     * @return a ResponseEntity containing either the error details or an empty success body
     */
    public <ENTITY> ResponseEntity<?> buildResponse(ENTITY entity,
                                                    BindingResult br,
                                                    ThrowingConsumer<ENTITY> saveFunction) {
        return br.hasErrors() ? createErrorResponse(br) : acceptAndCreateSuccessResponse(saveFunction, entity);
    }

    /**
     * Executes the given consumer with the provided object and returns an OK response.
     * If the consumer throws an exception, an error response is returned.
     *
     * @param t        the object to pass to the consumer
     * @param consumer the consumer function (e.g., bookingService::delete)
     * @param <T>      the type of the object
     * @return a ResponseEntity with an OK status if successful, otherwise a bad request with error details
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
     * Executes the given bi-consumer with the provided objects and returns an OK response.
     * If the consumer throws an exception, an error response is returned.
     *
     * @param t        the first parameter for the consumer
     * @param r        the second parameter for the consumer
     * @param consumer the bi-consumer function (e.g., bookingService::delete)
     * @param <T>      the type of the first parameter
     * @param <R>      the type of the second parameter
     * @return a ResponseEntity with an OK status if successful, otherwise a bad request with error details
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
     * Creates a ResponseEntity by first checking for binding errors.
     * If no errors are found, the supplier is used to obtain an additional parameter,
     * and then the bi-consumer is executed using both the original and supplied parameters.
     *
     * @param t         the primary object for processing
     * @param br        the BindingResult to check for validation errors
     * @param supplier  a supplier to provide an additional parameter if validation passes
     * @param consumer  the bi-consumer to execute if no errors occur (e.g., bookingService::delete)
     * @param <T>       the type of the first parameter
     * @param <R>       the type supplied by the supplier
     * @return a ResponseEntity with an OK status if successful, otherwise a bad request with error details
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
     * Executes the given supplier and bi-consumer using the provided object.
     * If the supplier or consumer throws an exception, an error response is returned.
     *
     * @param t         the object to process
     * @param supplier  a supplier to provide an additional parameter
     * @param consumer  the bi-consumer function to execute (e.g., bookingService::delete)
     * @param <T>       the type of the first parameter
     * @param <R>       the type supplied by the supplier
     * @return a ResponseEntity with an OK status if successful, otherwise a bad request with error details
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
     * Executes the given supplier and bi-supplier using the provided object,
     * then wraps the result from the bi-supplier in a success response.
     * If any step throws an exception, an error response is returned.
     *
     * @param t          the primary object for processing
     * @param supplier   a supplier to provide an additional parameter
     * @param biSupplier the bi-supplier function to execute (e.g., bookingService::delete)
     * @param <T>        the type of the first parameter
     * @param <R>        the type supplied by the supplier
     * @param <U>        the type returned by the bi-supplier
     * @return a ResponseEntity containing the result wrapped in a success map, or an error response if an exception occurs
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
     * Executes the given supplier and applies a function to the supplied result,
     * returning the result in an OK response.
     * If an exception occurs, an error response is returned.
     *
     * @param supplier a supplier to obtain a result
     * @param function a function to process the supplied result (e.g., orderService::findAll)
     * @param <T>      the type supplied by the supplier
     * @return a ResponseEntity containing the function's output, or an error response if an exception occurs
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
     * Executes the given supplier and returns the result in an OK response.
     * If an exception occurs, an error response is returned.
     *
     * @param supplier a supplier to obtain a result
     * @param <T>      the type supplied by the supplier
     * @return a ResponseEntity containing the function's output, or an error response if an exception occurs
     */
    public <T> ResponseEntity<?> buildResponse(ThrowingSupplier<T> supplier) {
        try {
            T t = supplier.get();
            return ResponseEntity.ok(t);
        } catch (Exception e) {
            return createErrorResponse(e);
        }
    }

    /**
     * Retrieves an object by applying a function to the provided parameter
     * and returns the result in an OK response.
     * If an exception occurs, an error response is returned.
     *
     * @param t        the input parameter for the function
     * @param function the function to execute (e.g., orderService::findByTable)
     * @param <T>      the type of the input parameter
     * @param <R>      the type of the result
     * @return a ResponseEntity containing the retrieved object or an error response if an exception occurs
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
     * Retrieves an entity from the database using its ID by executing the provided function.
     * On success, the entity is wrapped in a success response; on failure, an error response is returned.
     *
     * @param id       the ID of the entity to retrieve
     * @param function the function to fetch the entity (e.g., userService::findById)
     * @param <ID>     the type of the entity ID
     * @param <ENTITY> the type of the entity
     * @return a ResponseEntity containing the entity wrapped in a success map, or an error response if an exception occurs
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
     * Executes the given consumer using the provided ID and returns an OK response.
     * If the consumer throws an exception, an error response is returned.
     *
     * @param id       the ID of the entity to process
     * @param function the consumer function to execute (e.g., orderService::finishDineIn)
     * @param <ID>     the type of the entity ID
     * @return a ResponseEntity with an OK status if successful, or a bad request with error details if an exception occurs
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
     * Extracts field errors from the provided BindingResult.
     * <p>
     * This method should be called after checking {@code br.hasErrors()}.
     *
     * @param br the BindingResult containing field errors
     * @return a map where the keys are field names and the values are the corresponding error messages
     */
    public Map<String, String> getFieldErrors(BindingResult br) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : br.getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }
        return fieldErrors;
    }

    /**
     * Retrieves a paginated collection of entities filtered by a date range.
     * <p>
     * The requestBody must contain the following keys:
     * <ul>
     *   <li>"pageNumber" (Integer) - the page number</li>
     *   <li>"pageSize" (Integer) - the size of the page</li>
     *   <li>"dateFrom" (String) - the start date in ISO format</li>
     *   <li>"dateTo" (String) - the end date in ISO format</li>
     * </ul>
     *
     * @param requestBody a map containing the pagination and date range parameters
     * @param getByDate   a function to fetch entities by date range given a Pageable, start date, and end date
     * @param <T>         the type of entities in the page
     * @return a ResponseEntity containing the paginated collection of entities
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

    /**
     * Creates an error ResponseEntity based on the given exception.
     * <p>
     * The error is logged and the response contains a map with the key "exceptionMsg" and the exception's message.
     *
     * @param e the exception that occurred
     * @return a ResponseEntity with a bad request status and error details
     */
    public ResponseEntity<Map<String, Object>> createErrorResponse(Exception e) {
        log.error("An error occurred: {}", e.getMessage(), e);
        Map<String, Object> params = Map.of("exceptionMsg", e.getMessage());
        return ResponseEntity.badRequest().body(params);
    }

    /**
     * Validates a RegistrationDTO and returns a map of error parameters if validation fails.
     * <p>
     * If the username already exists, the map will contain an entry for "givenUsername" and an error message for "username".
     * If the provided passwords do not match, the map will contain an error message for "repeatedPassword".
     *
     * @param registrationDTO the registration data transfer object
     * @return a map containing error details; an empty map if no errors are found
     */
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

    /**
     * Creates a redirection ResponseEntity.
     * <p>
     * The provided URL is appended to the base CMS application URL defined by {@code cmsAppUrl}.
     *
     * @param url the relative URL to redirect to
     * @return a ResponseEntity with a 302 FOUND status and the "Location" header set
     */
    public ResponseEntity<?> redirectTo(String url) {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(cmsAppUrl + url))
                .build();
    }

    /**
     * Creates an error ResponseEntity using the field errors from the given BindingResult.
     *
     * @param br the BindingResult containing validation errors
     * @return a ResponseEntity with a bad request status and a map of field errors
     */
    public ResponseEntity<?> createErrorResponse(BindingResult br) {
        return ResponseEntity.badRequest().body(getFieldErrors(br));
    }

    /**
     * Creates a success ResponseEntity by wrapping the given entity in a map.
     * <p>
     * The entity is stored in the map with a key that is the lower-case version of its simple class name.
     *
     * @param entity   the entity to include in the response
     * @param <ENTITY> the type of the entity
     * @return a ResponseEntity with an OK status containing the entity in a map
     */
    private <ENTITY> ResponseEntity<Map<String, Object>> createSuccessResponse(ENTITY entity) {
        String simpleClassName = entity.getClass().getSimpleName();
        String paramName = Character.toLowerCase(simpleClassName.charAt(0)) + simpleClassName.substring(1);
        Map<String, Object> params = Map.of(paramName, entity);
        return ResponseEntity.ok(params);
    }

    /**
     * Executes the provided save function on the given entity and returns a success response.
     * <p>
     * If the save function throws an exception, an error response is returned with the exception message.
     *
     * @param saveFunction the function to persist the entity
     * @param entity       the entity to save
     * @param <ENTITY>     the type of the entity
     * @return an OK ResponseEntity with an empty map if successful, otherwise a bad request with error details
     */
    private <ENTITY> ResponseEntity<Map<String, Object>> acceptAndCreateSuccessResponse(ThrowingConsumer<ENTITY> saveFunction, ENTITY entity) {
        try {
            saveFunction.accept(entity);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("exceptionMsg", e.getMessage()));
        }
        return ResponseEntity.ok().body(new HashMap<>());
    }
}