package pl.rarytas.rarytas_restaurantside.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.multipart.MultipartFile;
import pl.rarytas.rarytas_restaurantside.entity.MenuItem;
import pl.rarytas.rarytas_restaurantside.service.interfaces.MenuItemService;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

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
        params.put("success", true);
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
        params.put("success", true);
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
