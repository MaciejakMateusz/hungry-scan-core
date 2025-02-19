package com.hackybear.hungry_scan_core.integration.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hackybear.hungry_scan_core.dto.MenuSimpleDTO;
import com.hackybear.hungry_scan_core.dto.RestaurantDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest(properties = {"spring.profiles.active=test"})
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RedisSerializationTest {

    @Test
    public void testSerialization() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        Set<RestaurantDTO> originalSet = new HashSet<>();
        originalSet.add(createRestaurantDTO());

        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);
        Set<?> deserialized = getDeserializedSet(serializer, originalSet);
        String expectedJson = objectMapper.writeValueAsString(originalSet);
        String actualJson = objectMapper.writeValueAsString(deserialized);

        assertEquals(objectMapper.readTree(expectedJson), objectMapper.readTree(actualJson));
    }

    private RestaurantDTO createRestaurantDTO() {
        return new RestaurantDTO(
                1L,
                "token123",
                "Restaurant Name",
                "Address",
                "40-404",
                "Katowice",
                getMenuSimpleDTOs(),
                Instant.now());
    }

    private Set<MenuSimpleDTO> getMenuSimpleDTOs() {
        return Set.of(
                new MenuSimpleDTO(1L, "Menu1", null, true)
        );
    }

    private Set<?> getDeserializedSet(GenericJackson2JsonRedisSerializer serializer, Set<RestaurantDTO> originalSet) {
        byte[] serialized = serializer.serialize(originalSet);
        Object deserializedObj = serializer.deserialize(serialized);
        if (deserializedObj instanceof Collection<?>) {
            return new HashSet<>((Collection<?>) deserializedObj);
        }
        return new HashSet<>();
    }
}