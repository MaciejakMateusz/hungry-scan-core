package com.hackybear.hungry_scan_core.integration.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
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
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer();
        ObjectMapper objectMapper = new ObjectMapper();

        Set<RestaurantDTO> originalSet = new HashSet<>();
        originalSet.add(new RestaurantDTO(1, "token123", "Restaurant Name", "Address"));

        byte[] serialized = serializer.serialize(originalSet);
        Set<RestaurantDTO> deserialized = (Set<RestaurantDTO>) serializer.deserialize(serialized);

        assertEquals(objectMapper.writeValueAsString(originalSet), objectMapper.writeValueAsString(deserialized));
    }
}