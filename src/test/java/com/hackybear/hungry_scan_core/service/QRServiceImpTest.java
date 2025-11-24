package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.controller.ResponseHelper;
import com.hackybear.hungry_scan_core.entity.*;
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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
    private S3Service s3Service;
    @Mock
    private ExceptionHelper exceptionHelper;
    @Mock
    private RestaurantTableService restaurantTableService;
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
        ReflectionTestUtils.setField(qrService, "qrPath", tempDir.toString());
        ReflectionTestUtils.setField(qrService, "customerAppUrl", "http://customer.app");
        ReflectionTestUtils.setField(qrService, "appUrl", "http://my.app");
        ReflectionTestUtils.setField(qrService, "isProduction", false);

        ReflectionTestUtils.setField(qrService, "qrPath", tempDir.toString());
        ReflectionTestUtils.setField(qrService, "qrName", "QR code - HungryScan");
    }

    @Test
    void generate_noArg_invokesUploadWithCorrectFile() throws Exception {
        long restaurantId = 2L;
        doNothing().when(s3Service).uploadFile(anyString(), any(MultipartFile.class));

        Restaurant restaurant = new Restaurant();
        restaurant.setId(restaurantId);
        restaurant.setQrVersion(1);
        restaurant.setToken("fakeToken");
        doReturn(Optional.of(restaurant)).when(restaurantRepository).findById(restaurantId);

        int expectedNewVersion = restaurant.getQrVersion() + 1;

        qrService.generate(restaurantId);

        String expectedKey = tempDir.toString().replace('\\', '/')
                + "/" + restaurantId + "/" + expectedNewVersion + ".png";

        ArgumentCaptor<MultipartFile> fileCaptor = ArgumentCaptor.forClass(MultipartFile.class);

        verify(s3Service).uploadFile(argThat(actual ->
                actual.replace('\\', '/').equals(expectedKey)
        ), fileCaptor.capture());

        MultipartFile qrFile = fileCaptor.getValue();
        assertNotNull(qrFile, "Should get a MultipartFile");
        assertEquals("QR code - HungryScan.png", qrFile.getOriginalFilename());
        assertEquals("image/png", qrFile.getContentType());
        assertTrue(qrFile.getBytes().length > 0, "QR payload should be non‐empty");
    }

    @Test
    void generate_withCustomName_setsTableNameAndSaves() throws Exception {
        RestaurantTable table = new RestaurantTable();
        table.setId(1L);
        table.setNumber(2);
        table.setToken("tkn123");

        String customName = "myQRCode";
        qrService.generate(table, customName);

        assertEquals(customName + ".png", table.getQrName());
        verify(restaurantTableService).save(table);
    }

    @Test
    void generate_emptyName_usesDefaultNamingAndSaves() throws Exception {
        RestaurantTable table = new RestaurantTable();
        table.setId(42L);
        table.setNumber(7);
        table.setToken("xyz");

        qrService.generate(table, "");

        String expected = "QR code - Table number 7, Table ID 42.png";
        assertEquals(expected, table.getQrName());
        verify(restaurantTableService).save(table);
    }

    @Test
    void createQrFile_returnsMultipartFile() throws Exception {
        String format = "png";
        String baseName = "testFile";
        String url = "http://example.com/scan/xyz";

        MultipartFile file = ReflectionTestUtils.invokeMethod(
                qrService,
                "createQrFile",
                format, baseName, url
        );

        assertNotNull(file);
        assertEquals("file", file.getName());
        assertEquals(baseName + ".png", file.getOriginalFilename());
        assertEquals("image/png", file.getContentType());
        assertTrue(file.getBytes().length > 0);
    }

    @Test
    void scanQRCode_invalidToken_redirectsToInvalid() throws IOException {
        when(restaurantRepository.existsByToken("bad")).thenReturn(false);
        HttpServletResponse resp = mock(HttpServletResponse.class);

        ResponseEntity<?> result = qrService.scanQRCode(resp, "bad");

        verify(resp).sendRedirect("http://customer.app/invalid-token");
        assertEquals(HttpStatus.PERMANENT_REDIRECT, result.getStatusCode());
    }
//
//    @Test
//    void scanQRCode_restaurantClosed_redirectsToRestaurantClosed() throws Exception {
//        // given
//        HttpServletResponse resp = mock(HttpServletResponse.class);
//
//        String token = "test-token";
//
//        Restaurant restaurant = new Restaurant();
//        restaurant.setId(1L);
//        restaurant.setToken(token);
//
//        when(restaurantRepository.existsByToken(token)).thenReturn(true);
//        when(restaurantRepository.findByToken(token)).thenReturn(Optional.of(restaurant));
//
//        when(jwtService.generateToken(anyString())).thenReturn("dummy-jwt");
//
//        // Make getActiveMenuId(...) return 0L by returning Optional.empty()
//        when(menuRepository.findActiveMenuId(any(), any(), eq(1L)))
//                .thenReturn(Optional.empty());
//
//        Role role = new Role();
//        role.setName("ROLE_CUSTOMER_READONLY");
//        when(roleService.findByName("ROLE_CUSTOMER_READONLY")).thenReturn(role);
//
//        // Let saveTempUser just return the same user instance it receives
//        when(userService.saveTempUser(any(User.class)))
//                .thenAnswer(invocation -> invocation.getArgument(0));
//
//        // when
//        ResponseEntity<?> result = qrService.scanQRCode(resp, token);
//
//        // then
//        verify(resp).sendRedirect("http://customer.app/restaurant-closed");
//        assertEquals(HttpStatus.PERMANENT_REDIRECT, result.getStatusCode());
//    }



    @Test
    void scanQRCode_persistUserThrows_returnsErrorResponse() throws IOException {
        when(restaurantRepository.existsByToken("tok")).thenReturn(true);
        when(jwtService.generateToken(anyString())).thenReturn("jwttoken");
        ResponseEntity<Map<String, Object>> badResponse = ResponseEntity.badRequest().build();
        when(responseHelper.createErrorResponse(any(Exception.class)))
                .thenReturn(badResponse);

        ResponseEntity<?> result = qrService.scanQRCode(mock(HttpServletResponse.class), "tok");
        assertSame(badResponse, result);
        verify(responseHelper).createErrorResponse(any(Exception.class));
    }

    @Test
    void scanQRCode_success_nonProduction_setsCookiesAndRedirect() throws Exception {
        when(restaurantRepository.existsByToken("rt")).thenReturn(true);
        when(jwtService.generateToken(anyString())).thenReturn("jwtval");
        Restaurant r = new Restaurant();
        r.setId(10L);
        when(menuRepository.findActiveMenuId(
                eq(LocalDate.now().getDayOfWeek()),
                any(LocalTime.class),
                eq(10L)
        )).thenReturn(Optional.of(99L));
        Role role = new Role();
        when(roleService.findByName("ROLE_CUSTOMER_READONLY")).thenReturn(role);
        when(userService.saveTempUser(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(restaurantRepository.findByToken("rt"))
                .thenReturn(Optional.of(r));

        HttpServletResponse resp = mock(HttpServletResponse.class);
        ResponseEntity<?> result = qrService.scanQRCode(resp, "rt");

        assertEquals(HttpStatus.PERMANENT_REDIRECT, result.getStatusCode());
        HttpHeaders h = result.getHeaders();
        assertEquals("http://customer.app", h.getFirst(HttpHeaders.LOCATION));
        List<String> cookies = h.get(HttpHeaders.SET_COOKIE);
        assertNotNull(cookies);
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
        Restaurant r = new Restaurant();
        r.setId(20L);
        when(menuRepository.findActiveMenuId(
                eq(LocalDate.now().getDayOfWeek()),
                any(LocalTime.class),
                eq(20L)
        )).thenReturn(Optional.of(55L));
        when(roleService.findByName("ROLE_CUSTOMER_READONLY")).thenReturn(new Role());
        when(userService.saveTempUser(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(restaurantRepository.findByToken("rt"))
                .thenReturn(Optional.of(r));

        ResponseEntity<?> result = qrService.scanQRCode(mock(HttpServletResponse.class), "rt");
        List<String> cookies = result.getHeaders().get(HttpHeaders.SET_COOKIE);
        assertNotNull(cookies);
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
                "footprint".equals(ev.getFootprint()) &&
                        ev.getRestaurantId() == 123L
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
