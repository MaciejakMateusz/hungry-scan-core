package pl.rarytas.rarytas_restaurantside.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.utility.ThrowingFunction;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


/**
 * This class provides support methods for building response entities.
 */
@Slf4j
@Component
public class ResponseHelper {

    /**
     * Creates ResponseEntity based on provided parameters.
     * If BindingResult has no errors it persists passed entity in database.
     *
     * @param entity       Entity to persist in database.
     * @param br           BindingResult created by using @Valid next to entity object in method parameters.
     *                     For example (@Valid User entity, BindingResult br) - br will hold errors if they occurred.
     * @param saveFunction Behaviour to pass from a given service. For example userService::save
     * @return ResponseEntity with appropriate response code and body containing parameters map.
     */
    public <T> ResponseEntity<Map<String, Object>> saveAndBuildResponseEntity(T entity,
                                                                              BindingResult br,
                                                                              Consumer<T> saveFunction) {
        Map<String, Object> params = new HashMap<>();
        if (br.hasErrors()) {
            params.put("errors", getFieldErrors(br));
            return ResponseEntity.badRequest().body(params);
        }
        saveFunction.accept(entity);
        return ResponseEntity.ok(params);
    }

    /**
     * Creates ResponseEntity based on provided parameters.
     * If BindingResult has no errors it persists passed entity in database.
     *
     * @param entity       Entity to persist in database.
     * @param r            Additional parameter to pass into service method.
     * @param br           BindingResult created by using @Valid next to entity object in method parameters.
     *                     For example (@Valid User entity, BindingResult br) - br will hold errors if they occurred.
     * @param saveFunction Behaviour to pass from a given service. For example userService::save
     * @return ResponseEntity with appropriate response code and body containing parameters map.
     */

    public <T, R> ResponseEntity<Map<String, Object>> saveAndBuildResponseEntity(T entity,
                                                                                 R r,
                                                                                 BindingResult br,
                                                                                 BiConsumer<T, R> saveFunction) {
        Map<String, Object> params = new HashMap<>();
        if (br.hasErrors()) {
            params.put("errors", getFieldErrors(br));
            return ResponseEntity.badRequest().body(params);
        }
        saveFunction.accept(entity, r);
        return ResponseEntity.ok(params);
    }

    /**
     * Finds entity in database based on provided ID.
     * Creates ResponseEntity based on provided parameters.
     *
     * @param id       ID of entity to get from a database.
     * @param function Behaviour to pass from a given service. For example userService::findById
     * @return ResponseEntity with appropriate response code and body containing parameters map.
     */

    public <T, R> ResponseEntity<Map<String, Object>> getResponseBody(T id, ThrowingFunction<T, R> function) {
        Map<String, Object> params = new HashMap<>();
        R r;
        try {
            r = function.apply(id);
        } catch (LocalizedException e) {
            params.put("exceptionMsg", e.getMessage());
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(params);
        }
        String paramName = r.getClass().getSimpleName().toLowerCase();
        params.put(paramName, r);
        return ResponseEntity.ok(params);
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
}
