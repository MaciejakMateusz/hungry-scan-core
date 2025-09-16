package com.hackybear.hungry_scan_core.integration.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hackybear.hungry_scan_core.dto.*;
import com.hackybear.hungry_scan_core.entity.PricePlan;
import com.hackybear.hungry_scan_core.entity.PricePlanType;
import com.hackybear.hungry_scan_core.enums.Language;
import com.hackybear.hungry_scan_core.enums.Theme;
import com.hackybear.hungry_scan_core.utility.Money;
import com.hackybear.hungry_scan_core.utility.TimeRange;
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

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest
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
        PricePlan pricePlan = new PricePlan();
        pricePlan.setId(1L);
        pricePlan.setPlanType(getPricePlanType());
        return new RestaurantDTO(
                1L,
                "token123",
                1,
                "Restaurant Name",
                "Address",
                "40-404",
                "Katowice",
                getMenuSimpleDTOs(),
                getSettingsDTO(),
                pricePlan,
                null,
                Instant.now(),
                LocalDateTime.now(),
                "Creation",
                "Modification");
    }

    private Set<MenuSimpleDTO> getMenuSimpleDTOs() {
        return Set.of(
                new MenuSimpleDTO(
                        1L,
                        1L,
                        "Menu1",
                        getMessageDTO(),
                        getMenuColorDTO(),
                        Theme.COLOR_090909.getHex(),
                        null,
                        true,
                        false)
        );
    }

    private SettingsDTO getSettingsDTO() {
        return new SettingsDTO(
                1L,
                1L,
                getOperatingHours(),
                2L,
                Language.PL,
                1200L,
                100L,
                (short) 150,
                true,
                false);
    }

    private Set<?> getDeserializedSet(GenericJackson2JsonRedisSerializer serializer, Set<RestaurantDTO> originalSet) {
        byte[] serialized = serializer.serialize(originalSet);
        Object deserializedObj = serializer.deserialize(serialized);
        if (deserializedObj instanceof Collection<?>) {
            return new HashSet<>((Collection<?>) deserializedObj);
        }
        return new HashSet<>();
    }

    private PricePlanType getPricePlanType() {
        PricePlanType pricePlanType = new PricePlanType();
        pricePlanType.setId(3L);
        pricePlanType.setName("best");
        pricePlanType.setPrice(Money.of(389.00));
        return pricePlanType;
    }

    private Map<DayOfWeek, TimeRange> getOperatingHours() {
        return Map.of(
                DayOfWeek.MONDAY, getDefaultTimeRange(),
                DayOfWeek.TUESDAY, getDefaultTimeRange(),
                DayOfWeek.WEDNESDAY, getDefaultTimeRange(),
                DayOfWeek.THURSDAY, getDefaultTimeRange(),
                DayOfWeek.FRIDAY, getDefaultTimeRange(),
                DayOfWeek.SATURDAY, getDefaultTimeRange(),
                DayOfWeek.SUNDAY, new TimeRange().withAvailable(false));
    }

    private TimeRange getDefaultTimeRange() {
        return new TimeRange(LocalTime.of(10, 0), LocalTime.of(22, 0));
    }

    private MenuColorDTO getMenuColorDTO() {
        return new MenuColorDTO(99L, "#000000");
    }

    private TranslatableDTO getMessageDTO() {
        return new TranslatableDTO(245L, "Witaj!", "Welcome!", null, null, null, null);
    }
}