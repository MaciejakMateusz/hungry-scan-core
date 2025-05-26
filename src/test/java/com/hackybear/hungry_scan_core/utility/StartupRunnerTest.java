package com.hackybear.hungry_scan_core.utility;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.DefaultApplicationArguments;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class StartupRunnerTest {

    private StartupRunner runner;
    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    void setUp() {
        runner = new StartupRunner();

        Logger logger = (Logger) org.slf4j.LoggerFactory.getLogger(StartupRunner.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @Test
    void run_shouldLogTwoInfoMessages() {
        ApplicationArguments args = new DefaultApplicationArguments();

        runner.run(args);

        List<ILoggingEvent> logs = listAppender.list;
        assertThat(logs).hasSize(2);

        assertThat(logs.get(0).getLevel().toString()).isEqualTo("INFO");
        assertThat(logs.get(0).getFormattedMessage())
                .isEqualTo("Executing start up actions...");

        assertThat(logs.get(1).getLevel().toString()).isEqualTo("INFO");
        assertThat(logs.get(1).getFormattedMessage())
                .isEqualTo("Start up actions completed");
    }
}
