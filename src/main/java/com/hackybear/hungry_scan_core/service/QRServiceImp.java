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
import com.hackybear.hungry_scan_core.exception.LocalizedException;
import com.hackybear.hungry_scan_core.service.interfaces.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
@Slf4j
public class QRServiceImp implements QRService {


    @Value("${QR_PATH}")
    private String directory;

    @Value("${server.port}")
    private String port;

    @Value("${CUSTOMER_APP_URL}")
    private String customerAppUrl;

    @Value("${IS_PROD}")
    private boolean isProduction;

    private static final String GENERAL_QR_NAME = "QR code - HungryScan";

    private final RestaurantTableService restaurantTableService;
    private final RestaurantService restaurantService;
    private final RestaurantMapper restaurantMapper;
    private final RoleService roleService;
    private final JwtService jwtService;
    private final ResponseHelper responseHelper;
    private final UserService userService;

    public QRServiceImp(RestaurantTableService restaurantTableService, RestaurantService restaurantService, RestaurantMapper restaurantMapper, RoleService roleService, JwtService jwtService, ResponseHelper responseHelper, UserService userService) {
        this.restaurantTableService = restaurantTableService;
        this.restaurantService = restaurantService;
        this.restaurantMapper = restaurantMapper;
        this.roleService = roleService;
        this.jwtService = jwtService;
        this.responseHelper = responseHelper;
        this.userService = userService;
    }

    @Override
    public void generate() throws Exception {
        log.info("Generating QR code...");

        String format = "png";

        StringBuilder urlBuilder = getEndpointAddress();
        String url = urlBuilder.toString();

        createQrFile(format, GENERAL_QR_NAME, url);

        log.info("QR code generated successfully.");
    }

    @Override
    public void generate(RestaurantTable table, String name) throws Exception {
        StringBuilder urlBuilder = getEndpointAddress();
        urlBuilder.append(table.getToken());
        String url = urlBuilder.toString();

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
    public boolean generalQrExists() {
        File qr = new File(directory + GENERAL_QR_NAME);
        return qr.exists();
    }

    @Override
    public ResponseEntity<?> scanQRCode(HttpServletResponse response, String restaurantToken) throws IOException {
        String username = UUID.randomUUID().toString().substring(1, 13) + "@temp.it";
        String jwt = jwtService.generateToken(username);
        try {
            persistUser(new JwtToken(jwt), username, restaurantToken);
        } catch (Exception e) {
            return responseHelper.createErrorResponse(e);
        }

        String cookie = prepareJwtCookie(jwt);
        response.addHeader("Set-Cookie", cookie);
        response.sendRedirect(customerAppUrl);
        return ResponseEntity.status(HttpStatus.PERMANENT_REDIRECT).build();
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

    private StringBuilder getEndpointAddress() {
        StringBuilder sb = new StringBuilder();
        sb.append("http://").append(getServerIPAddress()).append(":").append(port).append("/api/scan");
        return sb;
    }

    private String getServerIPAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            System.out.println("Failed to determine server IP address: " + e.getMessage());
            return null;
        }
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
        temp.setRestaurants(Set.of(restaurant));
        temp.setActiveRestaurantId(restaurant.getId());
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
}
