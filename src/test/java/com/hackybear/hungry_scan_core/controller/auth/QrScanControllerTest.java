package com.hackybear.hungry_scan_core.controller.auth;

import com.hackybear.hungry_scan_core.entity.QrScanEvent;
import com.hackybear.hungry_scan_core.entity.Role;
import com.hackybear.hungry_scan_core.entity.User;
import com.hackybear.hungry_scan_core.repository.MenuRepository;
import com.hackybear.hungry_scan_core.repository.QrScanEventRepository;
import com.hackybear.hungry_scan_core.repository.UserRepository;
import com.hackybear.hungry_scan_core.test_utils.ApiJwtRequestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class QrScanControllerTest {

    @Autowired
    private ApiJwtRequestUtils apiRequestUtils;

    @Value("${CUSTOMER_APP_URL}")
    private String customerAppUrl;

    @Autowired
    private QrScanEventRepository qrScanEventRepository;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    @Autowired
    private MenuRepository menuRepository;

    @Order(1)
    @Sql({"/data-h2.sql", "/test-packs/qr-scans.sql"})
    @Test
    void init() {
        log.info("Initializing H2 database...");
    }

    @Test
    @Transactional
    @Rollback
    void shouldScanQr_expectSpecificResponse() throws Exception {
        String restaurantToken = "3d90381d-80d2-48f8-80b3-d237d5f0a8ed";

        Mockito.when(menuRepository.findActiveMenuId(any(), any(), any()))
                .thenReturn(Optional.of(1L));
        MockHttpServletResponse response = apiRequestUtils.executeGet("/api/qr/scan/" + restaurantToken);

        assertEquals(2, response.getCookies().length);
        assertEquals("jwt", response.getCookies()[0].getName());
        assertEquals("restaurantToken", response.getCookies()[1].getName());
        assertEquals("3d90381d-80d2-48f8-80b3-d237d5f0a8ed", response.getCookies()[1].getValue());
        assertEquals(308, response.getStatus());
        assertEquals("pl_PL", response.getLocale().toString());
        assertEquals(9, response.getHeaderNames().size());
        assertEquals(this.customerAppUrl, response.getHeader("Location"));
        assertNull(response.getErrorMessage());
    }

    @Test
    @Transactional
    @Rollback
    void shouldScanQr_expectNewCustomerDetails() throws Exception {
        List<User> currentRestaurantUsers = userRepository.findAllByActiveRestaurantId(1L);
        assertEquals(4, currentRestaurantUsers.size());
        String existingRestaurantToken = "3d90381d-80d2-48f8-80b3-d237d5f0a8ed";

        Mockito.when(menuRepository.findActiveMenuId(any(), any(), any()))
                .thenReturn(Optional.of(1L));
        apiRequestUtils.executeGet("/api/qr/scan/" + existingRestaurantToken);

        List<User> updatedRestaurantUsers = userRepository.findAllByActiveRestaurantId(1L);
        assertEquals(5, updatedRestaurantUsers.size());
        User newTempUser = updatedRestaurantUsers.getLast();
        assertEquals(1L, newTempUser.getActiveRestaurantId());
        assertEquals(0L, newTempUser.getOrganizationId());
        assertNotNull(newTempUser.getJwtToken());

        Set<Role> roles = newTempUser.getRoles();
        assertEquals(1, roles.size());
        assertEquals("ROLE_CUSTOMER_READONLY", roles.iterator().next().getName());
    }

    @Test
    @Transactional
    @Rollback
    void shouldScanQr_wrongRestaurantToken() throws Exception {
        String restaurantToken = "3d90381d-80d2-48f8-80b3-hehe";
        MockHttpServletResponse response = apiRequestUtils.executeGet("/api/qr/scan/" + restaurantToken);
        assertEquals(302, response.getStatus());
        assertEquals("pl_PL", response.getLocale().toString());
        assertEquals(8, response.getHeaderNames().size());
        assertEquals(this.customerAppUrl + "/invalid-token", response.getHeader("Location"));
    }

    @Test
    @WithMockUser(username = "admin@example.com")
    @Transactional
    @Rollback
    void shouldExecutePostScanActions() throws Exception {
        String existingFootprint = "3d90381d-80d2-48f8-80b3-d237d5f0a8ed_A";
        List<QrScanEvent> qrScans = qrScanEventRepository.findByFootprint(existingFootprint);
        assertEquals(2, qrScans.size());
        QrScanEvent qrScan = qrScans.getFirst();
        assertEquals(1L, qrScan.getRestaurantId());

        apiRequestUtils.postAndExpect200("/api/qr/post-scan", existingFootprint);

        qrScans = qrScanEventRepository.findByFootprint(existingFootprint).stream().sorted().toList();
        assertEquals(3, qrScans.size());
        assertEquals(1L, qrScans.getLast().getRestaurantId());
    }
}