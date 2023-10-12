package pl.rarytas.rarytas_restaurantside.controller.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pl.rarytas.rarytas_restaurantside.entity.Category;
import pl.rarytas.rarytas_restaurantside.service.interfaces.CategoryServiceInterface;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CategoryRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryServiceInterface categoryService;

    private static final Gson jsonSerializer = new GsonBuilder().registerTypeAdapter(LocalDateTime.class,
            (JsonDeserializer<LocalDateTime>) (json, type, jsonDeserializationContext) -> {
                Instant instant = Instant.ofEpochMilli(json.getAsJsonPrimitive().getAsLong());
                return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            }).create();

    @Test
    @Order(1)
    public void shouldGetSpecificJsonFormat() throws Exception {
        String expectedCategoryJson = "{\"id\":1,\"name\":\"Przystawki\","
                + "\"description\":\"Rozpocznij swoją kulinarną podróż od pysznych przystawek, "
                + "które skradną Twoje podniebienie. Wybierz spośród aromatycznych krewetek marynowanych w cytrynie, "
                + "wyrafinowanego carpaccio z polędwicy wołowej lub chrupiących nachos z soczystym sosem serowym.\","
                + "\"menuItems\":[],\"created\":null,\"updated\":null}";

        mockMvc.perform(get("/api/categories/1")).andDo(print()).andExpect(content().json(expectedCategoryJson));
    }

    @Test
    @Order(2)
    public void shouldGetCategoryFromDatabase() {
        Category category = categoryService.findById(1).orElse(new Category());
        assertEquals("Przystawki", category.getName());
    }

    @Test
    @Order(3)
    public void shouldGetCategoryFromEndpoint() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/categories/1")).andReturn();
        String actualCategoryJson = result.getResponse().getContentAsString();
        // assertTrue(actualCategoryJson.contains("Przystawki"));
        Category category = jsonSerializer.fromJson(actualCategoryJson, Category.class);
        assertEquals("Przystawki", category.getName());
    }

    @Test
    @Order(4)
    public void shouldGetAllCategories() {
        List<Category> categories = categoryService.findAll();
        assertEquals(8, categories.size());
    }
}