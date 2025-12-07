package com.hackybear.hungry_scan_core.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.hackybear.hungry_scan_core.controller.ResponseHelper;
import com.hackybear.hungry_scan_core.entity.*;
import com.hackybear.hungry_scan_core.exception.ExceptionHelper;
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.record.InMemoryMultipartFile;
import com.hackybear.hungry_scan_core.repository.MenuRepository;
import com.hackybear.hungry_scan_core.repository.QrScanEventRepository;
import com.hackybear.hungry_scan_core.repository.RestaurantRepository;
import com.hackybear.hungry_scan_core.repository.RestaurantTableRepository;
import com.hackybear.hungry_scan_core.service.interfaces.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.*;

import static com.hackybear.hungry_scan_core.utility.Fields.USER_RESTAURANT;

@Service
@RequiredArgsConstructor
@Slf4j
public class QRServiceImp implements QRService {

    private final MenuRepository menuRepository;
    private final QrScanEventRepository qrScanEventRepository;
    private final RestaurantRepository restaurantRepository;
    private final S3Service s3Service;
    private final ExceptionHelper exceptionHelper;
    private final RestaurantTableService restaurantTableService;
    private final RestaurantTableRepository restaurantTableRepository;
    private final RoleService roleService;
    private final JwtService jwtService;
    private final ResponseHelper responseHelper;
    private final UserService userService;

    @Value("${qr.path}")
    private String qrPath;

    @Value("${qr.name}")
    private String qrName;

    @Value("${CUSTOMER_APP_URL}")
    private String customerAppUrl;

    @Value("${APP_URL}")
    private String appUrl;

    @Value("${IS_PROD}")
    private boolean isProduction;

    @Override
    @Transactional
    @CacheEvict(value = USER_RESTAURANT, key = "#restaurantId")
    public void generate(Long restaurantId) throws Exception {
        String format = "png";
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow();
        Integer newQrVersion = restaurant.getQrVersion() + 1;
        String keyPath = qrPath + "/" + restaurantId + "/" + newQrVersion + ".png";
        String url = appUrl + "/api/scan/" + restaurant.getToken();
        MultipartFile qrFile = createQrFile(format, qrName, url);
        s3Service.uploadFile(keyPath, qrFile);
        restaurant.setQrVersion(newQrVersion);
        restaurantRepository.save(restaurant);
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
            User user = persistUser(new JwtToken(jwt), username, restaurantToken);
            if (Objects.equals(0L, user.getActiveMenuId())) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                response.sendRedirect(customerAppUrl + "/restaurant-closed/" + restaurantToken);
                return ResponseEntity.status(HttpStatus.PERMANENT_REDIRECT).build();
            }
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

    @Override
    public ResponseEntity<Resource> downloadQr(Long restaurantId) throws LocalizedException {
        if (!restaurantRepository.existsById(restaurantId)) {
            exceptionHelper.throwLocalizedMessage("error.restaurantService.restaurantNotFound");
        }
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow();
        String keyPath = qrPath + "/" + restaurantId + "/" + restaurant.getQrVersion() + ".png";
        return s3Service.downloadFile(keyPath);
    }

    @Override
    public ResponseEntity<Resource> downloadQr(Long restaurantId, Long tableId) throws LocalizedException {
        if (!restaurantRepository.existsById(restaurantId)) {
            exceptionHelper.throwLocalizedMessage("error.restaurantService.restaurantNotFound");
        } else if (!restaurantTableRepository.existsById(tableId)) {
            exceptionHelper.throwLocalizedMessage("error.restaurantTableService.tableNotFound");
        }
        return s3Service.downloadFile(qrPath, restaurantId, tableId);
    }

    private MultipartFile createQrFile(String format,
                                       String fileName,
                                       String url) throws WriterException, IOException {
        int width = 1000;
        int height = 1000;

        Map<EncodeHintType, Object> hints = Map.of(EncodeHintType.CHARACTER_SET, "UTF-8");
        QRCodeWriter qrWriter = new QRCodeWriter();
        String variantUrl = getVariantUrl(url);
        BitMatrix matrix = qrWriter.encode(variantUrl, BarcodeFormat.QR_CODE, width, height, hints);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(matrix, format, outputStream);
        byte[] imageBytes = outputStream.toByteArray();

        String originalFileName = fileName + "." + format;
        String contentType = "image/" + format.toLowerCase();
        return new InMemoryMultipartFile(
                "file",
                originalFileName,
                contentType,
                imageBytes
        );
    }


    private String getVariantUrl(String baseUrl) {
        SecureRandom rng = new SecureRandom();
        String rid = Long.toString(Math.abs(rng.nextLong()), 36); // base36
        return baseUrl + (baseUrl.contains("?") ? "&" : "?") + "rid=" + rid;
    }

    private String prepareJwtCookie(String jwt) {
        ResponseCookie cookie = ResponseCookie.from("menu_jwt", jwt)
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

    private User persistUser(JwtToken jwtToken, String username, String restaurantToken) throws LocalizedException {
        User user = createTempCustomer(jwtToken, username, restaurantToken);
        return userService.saveTempUser(user);
    }

    private User createTempCustomer(JwtToken jwtToken,
                                    String username,
                                    String restaurantToken) throws LocalizedException {
        User temp = new User();
        Restaurant restaurant = restaurantRepository.findByToken(restaurantToken)
                .orElseThrow(exceptionHelper.supplyLocalizedMessage(
                        "error.restaurantService.restaurantNotFoundByToken"));
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

    private Long getActiveMenuId(Long restaurantId) {
        LocalDateTime current = LocalDateTime.now();
        DayOfWeek today = current.toLocalDate().getDayOfWeek();
        Optional<Long> activeMenuIdOptional =
                menuRepository.findActiveMenuId(today, current.toLocalTime(), restaurantId);
        return activeMenuIdOptional.orElse(0L);
    }
}
