package pl.rarytas.rarytas_restaurantside.utility;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TokenGenerator {
    public static String generateToken(Integer tableId, Integer restaurantId) {
        // Combine tableId and restaurantId to create a unique string
        String inputString = tableId + ":" + restaurantId;

        // Generate a UUID based on the input string
        UUID uuid = UUID.nameUUIDFromBytes(inputString.getBytes());

        // Convert the UUID to a string representation
        return uuid.toString();
    }

}