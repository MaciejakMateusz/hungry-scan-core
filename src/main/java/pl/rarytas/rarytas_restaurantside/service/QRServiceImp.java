package pl.rarytas.rarytas_restaurantside.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import pl.rarytas.rarytas_restaurantside.entity.RestaurantTable;
import pl.rarytas.rarytas_restaurantside.service.interfaces.QRService;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Service
public class QRServiceImp implements QRService {

    private final Environment env;

    public QRServiceImp(Environment env) {
        this.env = env;
    }

    @Override
    public File generate(RestaurantTable table) throws Exception {
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

        Path tempFile = Files.createTempFile(fileName, "." + format);
        File qrFile = tempFile.toFile();
        MatrixToImageWriter.writeToPath(bitMatrix, format, qrFile.toPath());

        System.out.println("QR code for table " + table.getNumber() + " generated successfully.");

        return qrFile;
    }

    private StringBuilder getEndpointAddress() {
        String port = env.getProperty("server.port");
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
