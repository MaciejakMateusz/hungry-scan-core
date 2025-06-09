package com.hackybear.hungry_scan_core.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.hackybear.hungry_scan_core.controller.ResponseHelper;
import com.hackybear.hungry_scan_core.dto.RestaurantDTO;
import com.hackybear.hungry_scan_core.dto.mapper.RestaurantMapper;
import com.hackybear.hungry_scan_core.entity.*;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.repository.MenuRepository;
import com.hackybear.hungry_scan_core.repository.QrScanEventRepository;
import com.hackybear.hungry_scan_core.repository.RestaurantRepository;
import com.hackybear.hungry_scan_core.service.interfaces.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class QRServiceImp implements QRService {

    private final MenuRepository menuRepository;
    @Value("${QR_PATH}")
    private String directory;

    @Value("${CUSTOMER_APP_URL}")
    private String customerAppUrl;

    @Value("${APP_URL}")
    private String appUrl;

    @Value("${IS_PROD}")
    private boolean isProduction;

    private static final String GENERAL_QR_NAME = "QR code - HungryScan";

    private final QrScanEventRepository qrScanEventRepository;
    private final RestaurantRepository restaurantRepository;
    private final ExceptionHelper exceptionHelper;
    private final RestaurantTableService restaurantTableService;
    private final RestaurantService restaurantService;
    private final RestaurantMapper restaurantMapper;
    private final RoleService roleService;
    private final JwtService jwtService;
    private final ResponseHelper responseHelper;
    private final UserService userService;

    @Override
    public void generate() throws Exception {
        String format = "png";
        String url = appUrl + "/api/scan";
        createQrFile(format, GENERAL_QR_NAME, url);
    }

    @Override
    public void generate(RestaurantTable table, String name) throws Exception {
        String url = appUrl + "/api/scan/" + table.getToken();

        String format = "png";
        String fileName;
        if (!Objects.equals("", name)) {
            fileName = name;
        } else {
            fileName = "QR code - " + "Table number " + table.getNumber() + ", Table ID " + table.getId();
        }

        createQrFile(format, fileName, url);

        table.setQrName(fileName + "." + format);
        restaurantTableService.save(table);

        log.info("QR code for table {} generated successfully.", table.getNumber());
    }

    @Override
    @Transactional
    public ResponseEntity<?> scanQRCode(HttpServletResponse response, String restaurantToken) throws IOException {
        if (!restaurantRepository.existsByToken(restaurantToken)) {
            response.sendRedirect(customerAppUrl + "/invalid-token");
            return ResponseEntity.status(HttpStatus.PERMANENT_REDIRECT).build();
        }
        String username = UUID.randomUUID().toString().substring(1, 13) + "@temp.it";
        String jwt = jwtService.generateToken(username);
        try {
            persistUser(new JwtToken(jwt), username, restaurantToken);
        } catch (Exception e) {
            return responseHelper.createErrorResponse(e);
        }

        String jwtCookie = prepareJwtCookie(jwt);
        String restaurantTokenCookie = getRestaurantTokenCookie(restaurantToken);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, customerAppUrl);
        headers.add(HttpHeaders.SET_COOKIE, jwtCookie);
        headers.add(HttpHeaders.SET_COOKIE, restaurantTokenCookie);
        return new ResponseEntity<>(headers, HttpStatus.PERMANENT_REDIRECT);
    }

    @Override
    @Transactional
    public ResponseEntity<?> persistScanEvent(String footprint) {
        try {
            QrScanEvent qrScanEvent = new QrScanEvent();
            qrScanEvent.setFootprint(footprint);
            qrScanEvent.setRestaurantId(userService.getActiveRestaurantId());
            qrScanEventRepository.save(qrScanEvent);
            return ResponseEntity.ok().build();
        } catch (LocalizedException e) {
            return ResponseEntity.badRequest()
                    .body(exceptionHelper.getLocalizedMsg("error.userService.userNotFound"));
        }
    }

    private void createQrFile(String format, String fileName, String url) throws WriterException, IOException {
        int width = 1000;
        int height = 1000;

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, width, height, hints);
        Files.createDirectories(Paths.get(directory));
        Path qrFilePath = Paths.get(directory, fileName + "." + format);
        MatrixToImageWriter.writeToPath(bitMatrix, format, qrFilePath);
    }

    private String prepareJwtCookie(String jwt) {
        ResponseCookie cookie = ResponseCookie.from("jwt", jwt)
                .path("/")
                .httpOnly(true)
                .secure(isProduction)
                .maxAge(10800)
                .sameSite(isProduction ? "None" : "Strict")
                .build();
        return cookie.toString();
    }

    private String getRestaurantTokenCookie(String value) {
        long maxAllowedAge = 400L * 24 * 60 * 60; // 400 days in seconds
        ResponseCookie cookie = ResponseCookie.from("restaurantToken", value)
                .path("/")
                .secure(isProduction)
                .maxAge(maxAllowedAge)
                .sameSite("none")
                .build();
        return cookie.toString();
    }

    private void persistUser(JwtToken jwtToken, String username, String restaurantToken) throws LocalizedException {
        User user = createTempCustomer(jwtToken, username, restaurantToken);
        userService.saveTempUser(user);
    }

    private User createTempCustomer(JwtToken jwtToken,
                                    String username,
                                    String restaurantToken) throws LocalizedException {
        User temp = new User();
        RestaurantDTO restaurantDTO = restaurantService.findByToken(restaurantToken);
        Restaurant restaurant = restaurantMapper.toRestaurant(restaurantDTO);
        Long restaurantId = restaurant.getId();
        Long activeMenuId = getActiveMenuId(restaurantId);
        temp.setRestaurants(Set.of(restaurant));
        temp.setActiveRestaurantId(restaurantId);
        temp.setActiveMenuId(activeMenuId);
        temp.setOrganizationId(0L);
        temp.setUsername(username);
        temp.setEmail(username);
        temp.setForename("Temp");
        temp.setSurname("Customer");
        temp.setPassword(UUID.randomUUID().toString());
        Role role = roleService.findByName("ROLE_CUSTOMER_READONLY");
        temp.setRoles(new HashSet<>(Collections.singletonList(role)));
        temp.setJwtToken(jwtToken);
        return temp;
    }

    private Long getActiveMenuId(Long restaurantId) throws LocalizedException {
        LocalDateTime current = LocalDateTime.now();
        DayOfWeek today = current.toLocalDate().getDayOfWeek();
        Optional<Long> activeMenuIdOptional =
                menuRepository.findActiveMenuId(today.name(), current.toLocalTime(), restaurantId);
        return activeMenuIdOptional.orElseThrow(
                exceptionHelper.supplyLocalizedMessage("error.qrService.menuNotFound"));
    }
}
