package pl.rarytas.rarytas_restaurantside.utility;

import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class TokenGenerator {
    public String generateToken(Integer tableId, Integer restaurantId) {
        String combinedIds = tableId + "-" + restaurantId;

        byte[] encodedBytes = Base64.getEncoder().encode(combinedIds.getBytes());
        return new String(encodedBytes);
    }

}