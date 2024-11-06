package com.hackybear.hungry_scan_core.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.hackybear.hungry_scan_core.entity.RestaurantTable;
import com.hackybear.hungry_scan_core.service.interfaces.QRService;
import com.hackybear.hungry_scan_core.service.interfaces.RestaurantTableService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class QRServiceImp implements QRService {


    @Value("${QR_PATH}")
    private String directory;

    @Value("${server.port}")
    private String port;

    private static final String GENERAL_QR_NAME = "QR code - HungryScan";

    private final RestaurantTableService restaurantTableService;

    public QRServiceImp(RestaurantTableService restaurantTableService) {
        this.restaurantTableService = restaurantTableService;
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
}
