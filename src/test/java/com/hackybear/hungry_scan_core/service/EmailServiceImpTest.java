package com.hackybear.hungry_scan_core.service;

import com.hackybear.hungry_scan_core.dto.mapper.UserMapper;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.util.Objects;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EmailServiceImpTest {

    @Mock
    private JavaMailSender emailSender;

    @Mock
    private UserMapper userMapper;

    private EmailServiceImp emailService;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        emailService = new EmailServiceImp(emailSender, userMapper);
        emailService.setNoReplyMail("noreply@example.com");
        emailService.setContactMail("contact@example.com");
        emailService.setApplicationUrl("https://example.com");
        emailService.setCmsUrl("https://cms.example.com");
    }

    @BeforeAll
    void beforeAll() {
        try {
            mocks = MockitoAnnotations.openMocks(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    void afterAll() {
        try {
            if (mocks != null) mocks.close();
        } catch (Exception e) {
            log.error("Error closing mocks: ", e);
        }
    }

    @Test
    void testContactForm() {
        String from = "user@example.com";
        String noReply = "noreply@example.com";
        String subject = "Test Subject";
        String text = "Test Message";

        doNothing().when(emailSender).send(any(SimpleMailMessage.class));

        emailService.contactForm(from, subject, text);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(emailSender, times(1)).send(captor.capture());
        SimpleMailMessage sentMessage = captor.getValue();

        assertEquals(noReply, sentMessage.getFrom());
        assertEquals(from, sentMessage.getReplyTo());
        assertEquals("contact@example.com", Objects.requireNonNull(sentMessage.getTo())[0]);
        assertEquals(subject, sentMessage.getSubject());
        assertEquals(text, sentMessage.getText());
    }

    @Test
    void testPasswordRecovery() throws MessagingException {
        validateMessageOutput((to, emailToken) -> {
            try {
                emailService.passwordRecovery(to, emailToken);
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        }, "HungryScan - Jednorazowy link do zmiany hasła");
    }

    @Test
    void testActivateAccount() throws MessagingException {
        validateMessageOutput((to, emailToken) -> {
            try {
                emailService.activateAccount(to, emailToken);
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        }, "HungryScan - Link aktywacyjny konta");
    }

    private void validateMessageOutput(BiConsumer<String, String> consumer, String expectedSubject) throws MessagingException {
        MimeMessage mockMimeMessage = new MimeMessage((Session) null);
        when(emailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        doNothing().when(emailSender).send(any(MimeMessage.class));

        String to = "user@example.com";
        String emailToken = "testToken";

        doNothing().when(emailSender).send(any(MimeMessage.class));

        consumer.accept(to, emailToken);

        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(emailSender, times(1)).send(captor.capture());
        MimeMessage sentMessage = captor.getValue();
        String addressFrom = sentMessage.getFrom()[0].toString();
        assertEquals("noreply@example.com", addressFrom);
        assertEquals(to, sentMessage.getRecipients(Message.RecipientType.TO)[0].toString());
        assertEquals(expectedSubject, sentMessage.getSubject());
    }
}