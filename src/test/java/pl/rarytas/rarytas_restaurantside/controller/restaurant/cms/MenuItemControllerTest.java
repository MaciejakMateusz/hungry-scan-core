package pl.rarytas.rarytas_restaurantside.controller.restaurant.cms;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import pl.rarytas.rarytas_restaurantside.entity.MenuItem;
import pl.rarytas.rarytas_restaurantside.service.interfaces.MenuItemService;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WithMockUser(roles = "ADMIN")
class MenuItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MenuItemService mockMenuItemService;

    @Test
    void testItemsList() throws Exception {
        mockMvc.perform(get("/restaurant/cms/items"))
                .andExpect(status().isOk())
                .andExpect(view().name("restaurant/cms/items/list"));
    }

    @Test
    void testAddItem() throws Exception {
        mockMvc.perform(get("/restaurant/cms/items/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("restaurant/cms/items/add"));
    }

    @Test
    void testUpdateItem() throws Exception {
        MenuItem menuItem = createMenuItem();

        MockMultipartFile file = new MockMultipartFile(
                "imageFile",
                "sample-image.jpg",
                "image/jpeg",
                "Mock image content".getBytes()
        );

        mockMvc.perform(multipart("/restaurant/cms/items/add")
                        .file(file)
                        .param("name", menuItem.getName())
                        .param("price", menuItem.getPrice().toString()))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void testDeleteItem() throws Exception {
        when(mockMenuItemService.findById(anyInt())).thenReturn(Optional.of(new MenuItem()));

        mockMvc.perform(post("/restaurant/cms/items/delete")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("restaurant/cms/items/delete"))
                .andExpect(model().attributeExists("menuItem"));
    }

    @Test
    void testRemoveItem() throws Exception {
        when(mockMenuItemService.findById(anyInt())).thenReturn(Optional.of(new MenuItem()));
        MenuItem menuItem = createMenuItem();
        menuItem.setName("Sample Item");
        menuItem.setPrice(BigDecimal.valueOf(10.99));

        mockMvc.perform(post("/restaurant/cms/items/remove")
                        .param("name", menuItem.getName())
                        .param("price", menuItem.getPrice().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/restaurant/cms/items"));
    }

    private MenuItem createMenuItem() {
        MenuItem menuItem = new MenuItem();
        menuItem.setName("Sample Item");
        menuItem.setPrice(BigDecimal.valueOf(10.99));
        return menuItem;
    }
}