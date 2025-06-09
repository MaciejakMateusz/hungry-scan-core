package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.controller.ResponseHelper;
import com.hackybear.hungry_scan_core.dto.RestaurantDTO;
import com.hackybear.hungry_scan_core.dto.mapper.RestaurantMapper;
import com.hackybear.hungry_scan_core.entity.QrScanEvent;
import com.hackybear.hungry_scan_core.entity.Restaurant;
import com.hackybear.hungry_scan_core.entity.RestaurantTable;
import com.hackybear.hungry_scan_core.entity.Role;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.MenuRepository;
import com.hackybear.hungry_scan_core.repository.QrScanEventRepository;
import com.hackybear.hungry_scan_core.repository.RestaurantRepository;
import com.hackybear.hungry_scan_core.service.interfaces.*;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QRServiceImpTest {

    @Mock
    private MenuRepository menuRepository;
    @Mock
    private QrScanEventRepository qrScanEventRepository;
    @Mock
    private RestaurantRepository restaurantRepository;
    @Mock
    private ExceptionHelper exceptionHelper;
    @Mock
    private RestaurantTableService restaurantTableService;
    @Mock
    private RestaurantService restaurantService;
    @Mock
    private RestaurantMapper restaurantMapper;
    @Mock
    private RoleService roleService;
    @Mock
    private JwtService jwtService;
    @Mock
    private ResponseHelper responseHelper;
    @Mock
    private UserService userService;

    @InjectMocks
    private QRServiceImp qrService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {

        ReflectionTestUtils.setField(qrService, "directory", tempDir.toString());
        ReflectionTestUtils.setField(qrService, "customerAppUrl", "http://customer.app");
        ReflectionTestUtils.setField(qrService, "appUrl", "http://my.app");

        ReflectionTestUtils.setField(qrService, "isProduction", false);
    }

    @Test
    void generate_noArg_createsGeneralQrFile() throws Exception {
        qrService.generate();
        Path file = tempDir.resolve("QR code - HungryScan.png");
        assertTrue(Files.exists(file), "General QR file should exist");
        assertTrue(Files.size(file) > 0, "General QR file should be non‐empty");
    }

    @Test
    void generate_withCustomName_createsFileAndSavesTable() throws Exception {
        RestaurantTable table = new RestaurantTable();
        table.setId(1L);
        table.setNumber(2);
        table.setToken("tkn123");

        String customName = "myQRCode";
        qrService.generate(table, customName);

        Path file = tempDir.resolve(customName + ".png");
        assertTrue(Files.exists(file), "Custom‐named QR file should exist");
        assertEquals(customName + ".png", table.getQrName(), "QR name set on table");
        verify(restaurantTableService).save(table);
    }

    @Test
    void generate_emptyName_usesDefaultNaming() throws Exception {
        RestaurantTable table = new RestaurantTable();
        table.setId(42L);
        table.setNumber(7);
        table.setToken("xyz");

        qrService.generate(table, "");

        String expected = "QR code - Table number 7, Table ID 42.png";
        Path file = tempDir.resolve(expected);
        assertTrue(Files.exists(file), "Default‐named QR file should exist");
        assertEquals(expected, table.getQrName());
        verify(restaurantTableService).save(table);
    }

    @Test
    void scanQRCode_invalidToken_redirectsToInvalid() throws IOException {
        when(restaurantRepository.existsByToken("bad")).thenReturn(false);
        HttpServletResponse resp = mock(HttpServletResponse.class);

        ResponseEntity<?> result = qrService.scanQRCode(resp, "bad");

        verify(resp).sendRedirect("http://customer.app/invalid-token");
        assertEquals(HttpStatus.PERMANENT_REDIRECT, result.getStatusCode());
    }

    @Test
    void scanQRCode_persistUserThrows_returnsErrorResponse() throws IOException, LocalizedException {
        when(restaurantRepository.existsByToken("tok")).thenReturn(true);
        when(jwtService.generateToken(anyString())).thenReturn("jwttoken");

        when(restaurantService.findByToken("tok"))
                .thenThrow(new RuntimeException("boom"));

        ResponseEntity<Map<String, Object>> badResponse = ResponseEntity.badRequest().build();
        when(responseHelper.createErrorResponse(any(Exception.class)))
                .thenReturn(badResponse);

        ResponseEntity<?> result = qrService.scanQRCode(mock(HttpServletResponse.class), "tok");
        assertSame(badResponse, result, "Should return whatever responseHelper.createErrorResponse produced");
        verify(responseHelper).createErrorResponse(any(Exception.class));
    }

    @Test
    void scanQRCode_success_nonProduction_setsCookiesAndRedirect() throws Exception {
        when(restaurantRepository.existsByToken("rt")).thenReturn(true);
        when(jwtService.generateToken(anyString())).thenReturn("jwtval");

        RestaurantDTO dto = mock(RestaurantDTO.class);
        when(restaurantService.findByToken("rt")).thenReturn(dto);
        Restaurant r = new Restaurant();
        r.setId(10L);
        when(restaurantMapper.toRestaurant(dto)).thenReturn(r);

        when(menuRepository.findActiveMenuId(
                DayOfWeek.MONDAY.name(),
                any(LocalTime.class),
                eq(10L)
        )).thenReturn(Optional.of(99L));

        Role role = new Role();
        when(roleService.findByName("ROLE_CUSTOMER_READONLY")).thenReturn(role);
        doNothing().when(userService).saveTempUser(any());

        HttpServletResponse resp = mock(HttpServletResponse.class);
        ResponseEntity<?> result = qrService.scanQRCode(resp, "rt");

        assertEquals(HttpStatus.PERMANENT_REDIRECT, result.getStatusCode());
        HttpHeaders h = result.getHeaders();
        assertEquals("http://customer.app", h.getFirst(HttpHeaders.LOCATION));

        List<String> cookies = h.get(HttpHeaders.SET_COOKIE);
        assert cookies != null;
        assertEquals(2, cookies.size());

        String jwtCookie = cookies.getFirst();
        assertTrue(jwtCookie.contains("jwt=jwtval"));
        assertTrue(jwtCookie.contains("HttpOnly"));
        assertTrue(jwtCookie.contains("SameSite=Strict"));

        String rtCookie = cookies.get(1);
        assertTrue(rtCookie.contains("restaurantToken=rt"));
        assertTrue(rtCookie.contains("SameSite=none"));
        assertFalse(rtCookie.contains("Secure"));
    }

    @Test
    void scanQRCode_success_production_cookiesAreSecureAndNone() throws Exception {
        ReflectionTestUtils.setField(qrService, "isProduction", true);

        when(restaurantRepository.existsByToken("rt")).thenReturn(true);
        when(jwtService.generateToken(anyString())).thenReturn("jwtval");

        RestaurantDTO dto = mock(RestaurantDTO.class);
        when(restaurantService.findByToken("rt")).thenReturn(dto);
        Restaurant r = new Restaurant();
        r.setId(20L);
        when(restaurantMapper.toRestaurant(dto)).thenReturn(r);

        when(menuRepository.findActiveMenuId(
                DayOfWeek.THURSDAY.name(),
                any(LocalTime.class),
                eq(20L)
        )).thenReturn(Optional.of(55L));

        when(roleService.findByName("ROLE_CUSTOMER_READONLY"))
                .thenReturn(new Role());
        doNothing().when(userService).saveTempUser(any());

        HttpServletResponse resp = mock(HttpServletResponse.class);
        ResponseEntity<?> result = qrService.scanQRCode(resp, "rt");

        List<String> cookies = result.getHeaders().get(HttpHeaders.SET_COOKIE);
        assert cookies != null;
        String jwtCookie = cookies.getFirst();
        assertTrue(jwtCookie.contains("Secure"));
        assertTrue(jwtCookie.contains("SameSite=None"));

        String rtCookie = cookies.get(1);
        assertTrue(rtCookie.contains("Secure"));
        assertTrue(rtCookie.contains("SameSite=none"));
    }

    @Test
    void persistScanEvent_success_savesEventAndReturnsOk() throws LocalizedException {
        when(userService.getActiveRestaurantId()).thenReturn(123L);

        ResponseEntity<?> result = qrService.persistScanEvent("footprint");
        assertEquals(HttpStatus.OK, result.getStatusCode());

        verify(qrScanEventRepository).save(argThat((QrScanEvent ev) ->
                "footprint".equals(ev.getFootprint()) && ev.getRestaurantId() == 123L
        ));
    }

    @Test
    void persistScanEvent_userNotFound_returnsBadRequestWithMessage() throws LocalizedException {
        when(userService.getActiveRestaurantId())
                .thenThrow(new LocalizedException("no user"));
        when(exceptionHelper.getLocalizedMsg("error.userService.userNotFound"))
                .thenReturn("localized msg");

        ResponseEntity<?> result = qrService.persistScanEvent("fp");
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("localized msg", result.getBody());
    }
}