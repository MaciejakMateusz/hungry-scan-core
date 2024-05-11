package com.hackybear.hungry_scan_core.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.hackybear.hungry_scan_core.entity.RestaurantTable;
import com.hackybear.hungry_scan_core.service.interfaces.QRService;
import com.hackybear.hungry_scan_core.service.interfaces.RestaurantTableService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Service
public class QRServiceImp implements QRService {


    @Value("${QR_PATH}")
    private String directory;

    @Value("${server.port}")
    private String port;

    private final RestaurantTableService restaurantTableService;

    public QRServiceImp(RestaurantTableService restaurantTableService) {
        this.restaurantTableService = restaurantTableService;
    }

    @Override
    public void generate(RestaurantTable table) throws Exception {
        StringBuilder address = getEndpointAddress();
        address.append(table.getToken());
        String url = address.toString();

        String format = "png";
        String fileName = "QR code - " + "Table number " + table.getNumber() + ", Table ID " + table.getId();

        int width = 1000;
        int height = 1000;

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, width, height, hints);
        Files.createDirectories(Paths.get(directory));
        Path qrFilePath = Paths.get(directory, fileName + "." + format);
        MatrixToImageWriter.writeToPath(bitMatrix, format, qrFilePath);

        table.setQrName(fileName + "." + format);
        restaurantTableService.save(table);

        System.out.println("QR code for table " + table.getNumber() + " generated successfully.");
    }

    private StringBuilder getEndpointAddress() {
        StringBuilder sb = new StringBuilder();
        sb.append("https://").append(getServerIPAddress()).append(":").append(port).append("/api/scan/");
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
