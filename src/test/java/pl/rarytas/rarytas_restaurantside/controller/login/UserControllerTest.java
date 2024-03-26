package pl.rarytas.rarytas_restaurantside.controller.login;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import pl.rarytas.rarytas_restaurantside.dto.AuthRequestDTO;
import pl.rarytas.rarytas_restaurantside.dto.JwtResponseDTO;
import pl.rarytas.rarytas_restaurantside.entity.RestaurantTable;
import pl.rarytas.rarytas_restaurantside.service.interfaces.RestaurantTableService;
import pl.rarytas.rarytas_restaurantside.test_utils.ApiJwtRequestUtils;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class UserControllerTest {

    @Autowired
    private ApiJwtRequestUtils apiRequestUtils;

    @Autowired
    private RestaurantTableService restaurantTableService;

    @Test
    @Transactional
    @Rollback
    void shouldAuthenticateAndLoginUser() throws Exception {
        AuthRequestDTO authRequestDTO = new AuthRequestDTO("mati", "Lubieplacki123!");

        JwtResponseDTO jwtResponseDTO =
                apiRequestUtils.postAndFetchObject("/api/login", authRequestDTO, JwtResponseDTO.class);

        assertNotNull(jwtResponseDTO);
        assertEquals(129, jwtResponseDTO.getAccessToken().length());
    }

    @Test
    void shouldLoginAndReturnUnauthorized() throws Exception {
        AuthRequestDTO authRequestDTO = new AuthRequestDTO("iDoNotExist", "DoesNotMatter123!");
        apiRequestUtils.postAndExpectUnauthorized("/api/login", authRequestDTO);
    }

    @Test
    @Transactional
    @Rollback
    void shouldAuthenticateForCollaborativeOrdering() throws Exception {
        shouldAuthenticateQrScanForTable1FirstCustomer();
        shouldAuthenticateQrScanForTable1SecondCustomer();
    }

    @Test
    @Transactional
    @Rollback
    void shouldAuthenticateQrScanForTable6AndCallWaiter() throws Exception {
        RestaurantTable restaurantTable = restaurantTableService.findById(6);
        restaurantTable.setActive(true);
        restaurantTableService.save(restaurantTable);

        String tableToken = restaurantTable.getToken();
        JwtResponseDTO jwtResponseDTO =
                apiRequestUtils.fetchObjectWithParam("/api/scan/" + tableToken, JwtResponseDTO.class);

        assertNotNull(jwtResponseDTO);
        assertEquals(140, jwtResponseDTO.getAccessToken().length());

        apiRequestUtils.patchAndExpect200("/api/restaurant/tables/call-waiter", 6, jwtResponseDTO.getAccessToken());
        restaurantTable = restaurantTableService.findById(6);
        assertTrue(restaurantTable.isWaiterCalled());
    }

    private void shouldAuthenticateQrScanForTable1FirstCustomer() throws Exception {
        RestaurantTable restaurantTable = restaurantTableService.findById(3);
        assertFalse(restaurantTable.isActive());
        assertTrue(restaurantTable.getUsers().isEmpty());

        restaurantTable.setActive(true);
        restaurantTableService.save(restaurantTable);

        String tableToken = restaurantTable.getToken();
        JwtResponseDTO jwtResponseDTO =
                apiRequestUtils.fetchObjectWithParam("/api/scan/" + tableToken, JwtResponseDTO.class);

        assertNotNull(jwtResponseDTO);
        assertEquals(140, jwtResponseDTO.getAccessToken().length());

        restaurantTable = restaurantTableService.findById(3);
        assertEquals(1, restaurantTable.getUsers().size());
        assertEquals(12, restaurantTable.getUsers()
                .stream()
                .findFirst()
                .orElseThrow()
                .getUsername()
                .length());

        assertEquals(20, restaurantTable.getUsers()
                .stream()
                .findFirst()
                .orElseThrow()
                .getEmail()
                .length());
    }

    private void shouldAuthenticateQrScanForTable1SecondCustomer() throws Exception {
        RestaurantTable restaurantTable = restaurantTableService.findById(3);
        assertTrue(restaurantTable.isActive());
        assertEquals(1, restaurantTable.getUsers().size());

        String tableToken = restaurantTable.getToken();
        JwtResponseDTO jwtResponseDTO =
                apiRequestUtils.fetchObjectWithParam("/api/scan/" + tableToken, JwtResponseDTO.class);

        assertNotNull(jwtResponseDTO);
        assertEquals(140, jwtResponseDTO.getAccessToken().length());

        restaurantTable = restaurantTableService.findById(3);
        assertEquals(2, restaurantTable.getUsers().size());
        assertEquals(12, restaurantTable.getUsers()
                .stream()
                .skip(1)
                .findFirst()
                .orElseThrow()
                .getUsername()
                .length());

        assertEquals(20, restaurantTable.getUsers()
                .stream()
                .skip(1)
                .findFirst()
                .orElseThrow()
                .getEmail()
                .length());
    }

}