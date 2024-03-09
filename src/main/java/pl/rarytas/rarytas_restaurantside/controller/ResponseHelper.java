package pl.rarytas.rarytas_restaurantside.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import pl.rarytas.rarytas_restaurantside.utility.ThrowingFunction;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


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
    public <ENTITY> ResponseEntity<Map<String, Object>> buildResponse(ENTITY entity,
                                                                 BindingResult br,
                                                                 Consumer<ENTITY> saveFunction) {
        return br.hasErrors() ? createErrorResponse(br) : saveAndCreateSuccessResponse(saveFunction, entity);
    }

    /**
     * Creates ResponseEntity based on provided parameters.
     * If BindingResult has no errors it persists passed entity in database.
     *
     * @param entity       Entity to persist in database.
     * @param p            Additional parameter to pass into service method.
     * @param br           BindingResult created by using @Valid next to entity object in method parameters.
     *                     For example (@Valid User entity, BindingResult br) - br will hold errors if they occurred.
     * @param saveFunction Behaviour to pass from a given service. For example userService::save
     * @return ResponseEntity with appropriate response code and body containing parameters map.
     */

    public <ENTITY, P> ResponseEntity<Map<String, Object>> buildResponse(ENTITY entity,
                                                                    P p,
                                                                    BindingResult br,
                                                                    BiConsumer<ENTITY, P> saveFunction) {
        return br.hasErrors() ? createErrorResponse(br) : saveAndCreateSuccessResponse(saveFunction, entity, p);
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

    private ResponseEntity<Map<String, Object>> createErrorResponse(BindingResult br) {
        Map<String, Object> params = Map.of("errors", getFieldErrors(br));
        return ResponseEntity.badRequest().body(params);
    }

    private ResponseEntity<Map<String, Object>> createErrorResponse(Exception e) {
        log.error(e.getMessage());
        Map<String, Object> params = Map.of("exceptionMsg", e.getMessage());
        return ResponseEntity.badRequest().body(params);
    }

    private <ENTITY> ResponseEntity<Map<String, Object>> createSuccessResponse(ENTITY entity) {
        String paramName = entity.getClass().getSimpleName().toLowerCase();
        Map<String, Object> params = Map.of(paramName, entity);
        return ResponseEntity.ok(params);
    }

    private <ENTITY> ResponseEntity<Map<String, Object>> saveAndCreateSuccessResponse(Consumer<ENTITY> saveFunction,
                                                                                      ENTITY entity) {
        saveFunction.accept(entity);
        return ResponseEntity.ok().body(new HashMap<>());
    }

    private <ENTITY, PARAM> ResponseEntity<Map<String, Object>> saveAndCreateSuccessResponse(BiConsumer<ENTITY, PARAM> saveFunction,
                                                                                         ENTITY entity,
                                                                                         PARAM param) {
        saveFunction.accept(entity, param);
        return ResponseEntity.ok().body(new HashMap<>());
    }
}