package com.hackybear.hungry_scan_core.controller.auth;

import com.hackybear.hungry_scan_core.entity.QrScan;
import com.hackybear.hungry_scan_core.entity.Role;
import com.hackybear.hungry_scan_core.entity.ScanDate;
import com.hackybear.hungry_scan_core.entity.User;
import com.hackybear.hungry_scan_core.repository.QrScanRepository;
import com.hackybear.hungry_scan_core.repository.UserRepository;
import com.hackybear.hungry_scan_core.test_utils.ApiJwtRequestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

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
    private QrScanRepository qrScanRepository;
    @Autowired
    private UserRepository userRepository;

    @Order(1)
    @Sql("/data-h2.sql")
    @Test
    void init() {
        log.info("Initializing H2 database...");
    }

    @Test
    @Transactional
    @Rollback
    void shouldScanQr_expectSpecificResponse() throws Exception {
        String restaurantToken = "3d90381d-80d2-48f8-80b3-d237d5f0a8ed";
        MockHttpServletResponse response = apiRequestUtils.getResponse("/api/qr/scan/" + restaurantToken);
        assertEquals(2, response.getCookies().length);
        assertEquals("jwt", response.getCookies()[0].getName());
        assertEquals("restaurantToken", response.getCookies()[1].getName());
        assertEquals("3d90381d-80d2-48f8-80b3-d237d5f0a8ed", response.getCookies()[1].getValue());
        assertEquals(302, response.getStatus());
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
        assertEquals(5, currentRestaurantUsers.size());
        String existingRestaurantToken = "3d90381d-80d2-48f8-80b3-d237d5f0a8ed";

        apiRequestUtils.getResponse("/api/qr/scan/" + existingRestaurantToken);

        List<User> updatedRestaurantUsers = userRepository.findAllByActiveRestaurantId(1L);
        assertEquals(6, updatedRestaurantUsers.size());
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
        MockHttpServletResponse response = apiRequestUtils.getResponse("/api/qr/scan/" + restaurantToken);
        assertEquals(302, response.getStatus());
        assertEquals("pl_PL", response.getLocale().toString());
        assertEquals(8, response.getHeaderNames().size());
        assertEquals(this.customerAppUrl + "/invalid-token", response.getHeader("Location"));
    }

    @Test
    @Transactional
    @Rollback
    void shouldExecutePostScanActions_existingFootprint() throws Exception {
        String existingFootprint = "3d90381d-80d2-48f8-80b3-d237d5f0a8ed_e15e1e38-1d2e-48e0-8422-b6fbb1785ea8";
        Optional<QrScan> qrScanOptional = qrScanRepository.findByFootprint(existingFootprint);
        assertTrue(qrScanOptional.isPresent());
        QrScan qrScan = qrScanOptional.get();
        assertEquals(1, qrScan.getScanDates().size());
        assertEquals("3d90381d-80d2-48f8-80b3-d237d5f0a8ed", qrScan.getRestaurantToken());
        assertEquals(1, qrScan.getQuantity());

        apiRequestUtils.postAndExpect200("/api/qr/post-scan", existingFootprint);

        qrScanOptional = qrScanRepository.findByFootprint(existingFootprint);
        assertTrue(qrScanOptional.isPresent());
        qrScan = qrScanOptional.get();
        assertEquals(2, qrScan.getScanDates().size());
        assertEquals("3d90381d-80d2-48f8-80b3-d237d5f0a8ed", qrScan.getRestaurantToken());
        assertEquals(2, qrScan.getQuantity());
        List<ScanDate> scanDates = qrScan.getScanDates().stream().sorted().toList();
        assertEquals(LocalDate.now(), scanDates.getLast().getDate());
    }

    @Test
    @Transactional
    @Rollback
    void shouldExecutePostScanActions_uniqueFootprint() throws Exception {
        String newFootprint = "3d90381d-80d2-48f8-80b3-d237d5f0a8ed_e15e1e38-1erq-48e0-8321-b6fbb1785sa2";
        Optional<QrScan> qrScanOptional = qrScanRepository.findByFootprint(newFootprint);
        List<QrScan> qrScans = qrScanRepository.findAllByRestaurantToken("3d90381d-80d2-48f8-80b3-d237d5f0a8ed");
        assertTrue(qrScanOptional.isEmpty());
        assertEquals(3, qrScans.size());

        apiRequestUtils.postAndExpect200("/api/qr/post-scan", newFootprint);

        qrScanOptional = qrScanRepository.findByFootprint(newFootprint);
        qrScans = qrScanRepository.findAllByRestaurantToken("3d90381d-80d2-48f8-80b3-d237d5f0a8ed");
        assertTrue(qrScanOptional.isPresent());
        assertEquals(4, qrScans.size());
        QrScan qrScan = qrScanOptional.get();
        assertEquals(1, qrScan.getScanDates().size());
        assertEquals("3d90381d-80d2-48f8-80b3-d237d5f0a8ed", qrScan.getRestaurantToken());
        assertEquals(1, qrScan.getQuantity());
        List<ScanDate> scanDates = qrScan.getScanDates();
        assertEquals(LocalDate.now(), scanDates.getLast().getDate());
    }

}