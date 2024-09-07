package com.hackybear.hungry_scan_core.utility;

import com.hackybear.hungry_scan_core.service.interfaces.QRService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StartupRunner implements ApplicationRunner {

    private final QRService qrService;

    public StartupRunner(QRService qrService) {
        this.qrService = qrService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Executing StartupRunner...");
        if (!qrService.generalQrExists()) {
            qrService.generate();
        }
        log.info("StartupRunner completed.");
    }
}