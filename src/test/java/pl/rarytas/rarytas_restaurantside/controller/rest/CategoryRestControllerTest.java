package pl.rarytas.rarytas_restaurantside.controller.rest;

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
import pl.rarytas.rarytas_restaurantside.service.interfaces.CategoryService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CategoryRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryService categoryService;

//    private static final Gson jsonSerializer = new GsonBuilder().registerTypeAdapter(LocalDateTime.class,
//            (JsonDeserializer<LocalDateTime>) (json, type, jsonDeserializationContext) -> {
//                Instant instant = Instant.ofEpochMilli(json.getAsJsonPrimitive().getAsLong());
//                return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
//            }).create();

    @Test
    @Order(1)
    public void shouldGetSpecificJsonFormat() throws Exception {
        String expectedCategoryJson =
                "{\"id\":1,\"name\":\"Przystawki\",\"description\":\"Rozpocznij swoją kulinarną podróż od pysznych przystawek, które skradną Twoje podniebienie. Wybierz spośród aromatycznych krewetek marynowanych w cytrynie, wyrafinowanego carpaccio z polędwicy wołowej lub chrupiących nachos z soczystym sosem serowym.\"," +
                        "\"menuItems\":[" +
                        "{\"id\":1,\"name\":\"Krewetki marynowane w cytrynie\",\"description\":\"Soczyste krewetki marynowane w aromatycznym sosie cytrynowym.\",\"ingredients\":\"Krewetki, cytryna, oliwa z oliwek, czosnek, przyprawy\",\"price\":19.99,\"created\":null,\"updated\":null,\"base64Image\":\"empty\",\"available\":true}," +
                        "{\"id\":2,\"name\":\"Carpaccio z polędwicy wołowej\",\"description\":\"Cienko pokrojona polędwica wołowa podana z rukolą, parmezanem i kaparami.\",\"ingredients\":\"Polędwica wołowa, rukola, parmezan, kapary, oliwa z oliwek\",\"price\":24.50,\"created\":null,\"updated\":null,\"base64Image\":\"empty\",\"available\":true}," +
                        "{\"id\":3,\"name\":\"Krewetki w tempurze\",\"description\":\"Delikatne krewetki w cieście tempura, podawane z sosem słodko-kwaśnym\",\"ingredients\":\"Krewetki, mąka, jajko, olej roślinny, sos słodko-kwaśny\",\"price\":22.00,\"created\":null,\"updated\":null,\"base64Image\":\"empty\",\"available\":true}," +
                        "{\"id\":4,\"name\":\"Roladki z bakłażana z feta i suszonymi pomidorami\",\"description\":\"Bakłażany zawijane w roladki z feta i suszonymi pomidorami, pieczone w piecu.\",\"ingredients\":\"Bakłażan, ser feta, suszone pomidory, oliwa z oliwek\",\"price\":18.75,\"created\":null,\"updated\":null,\"base64Image\":\"empty\",\"available\":true}," +
                        "{\"id\":5,\"name\":\"Nachos z sosem serowym\",\"description\":\"Chrupiące nachos z sosem serowym, podane z guacamole i pikantnym sosem salsa.\",\"ingredients\":\"Nachos, ser, śmietana, awokado, pomidory, cebula, papryczki chili\",\"price\":16.99,\"created\":null,\"updated\":null,\"base64Image\":\"empty\",\"available\":true}],\"created\":null,\"updated\":null}";
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
        assertTrue(actualCategoryJson.contains("Przystawki"));
    }

    @Test
    @Order(4)
    public void shouldGetAllCategories() {
        List<Category> categories = categoryService.findAll();
        assertEquals(8, categories.size());
    }
}