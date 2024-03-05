package pl.rarytas.rarytas_restaurantside.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.multipart.MultipartFile;
import pl.rarytas.rarytas_restaurantside.entity.MenuItem;
import pl.rarytas.rarytas_restaurantside.entity.User;
import pl.rarytas.rarytas_restaurantside.exception.LocalizedException;
import pl.rarytas.rarytas_restaurantside.service.interfaces.MenuItemService;
import pl.rarytas.rarytas_restaurantside.service.interfaces.UserService;
import pl.rarytas.rarytas_restaurantside.utility.ThrowingFunction;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
@Component
public class ResponseHelper {

    public <T> ResponseEntity<Map<String, Object>> buildResponseEntity(T entity,
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

    public ResponseEntity<Map<String, Object>> buildResponseEntity(Map<String, Object> mappedRequest,
                                                                   BindingResult br,
                                                                   MenuItemService service) {
        Map<String, Object> params = new HashMap<>();
        if (br.hasErrors()) {
            params.put("errors", getFieldErrors(br));
            return ResponseEntity.badRequest().body(params);
        }
        MenuItem menuItem = (MenuItem) mappedRequest.get("menuItem");
        MultipartFile imageFile = (MultipartFile) mappedRequest.get("imageFile");
        service.save(menuItem, imageFile);
        return ResponseEntity.ok(params);
    }

    public ResponseEntity<Map<String, Object>> buildResponseEntity(User user, BindingResult br, UserService userService) {
        Map<String, Object> params = new HashMap<>();
        if (br.hasErrors()) {
            params.put("errors", getFieldErrors(br));
            return ResponseEntity.badRequest().body(params);
        } else if (userService.existsByEmail(user.getEmail())) {
            params.put("emailExists", true);
            return ResponseEntity.badRequest().body(params);
        } else if (!user.getPassword().equals(user.getRepeatedPassword())) {
            params.put("passwordsNotMatch", true);
            return ResponseEntity.badRequest().body(params);
        }
        userService.save(user);
        params.put("user", new User());
        return ResponseEntity.ok(params);
    }

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

    private Map<String, String> getFieldErrors(BindingResult br) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : br.getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }
        return fieldErrors;
    }
}
