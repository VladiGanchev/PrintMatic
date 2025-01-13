package com.example.printmatic.service;

import com.example.printmatic.enums.PageSize;
import com.example.printmatic.model.OrderEntity;
import com.example.printmatic.model.UserEntity;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private MailService mailService;

    private static final String TEST_HOST = "test@printmatic.com";
    private static final String TEST_TO = "user@example.com";
    private static final String TEST_SUBJECT = "Test Subject";
    private static final String TEST_BODY = "Test Body";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(mailService, "host", TEST_HOST);
    }

    @Test
    void sendEmail_Success() throws MessagingException {

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        assertDoesNotThrow(() -> mailService.sendEmail(TEST_TO, TEST_SUBJECT, TEST_BODY));

        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void sendEmail_ThrowsException() {

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("Failed to send email"))
                .when(mailSender)
                .send(any(MimeMessage.class));


        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> mailService.sendEmail(TEST_TO, TEST_SUBJECT, TEST_BODY));
        assertEquals("Failed to send email", exception.getMessage());
    }

    @Test
    void createHtmlBody_Success() {

        OrderEntity order = new OrderEntity();
        order.setId(1L);
        order.setTitle("Test Document");
        order.setCopies(5);
        order.setPageSize(PageSize.A4);
        order.setDoubleSided(true);
        order.setPrice(new BigDecimal("10.50"));

        UserEntity user = new UserEntity();
        user.setFirstName("John");
        user.setLastName("Doe");

        String htmlBody = mailService.createHtmlBody(order, user);

        assertNotNull(htmlBody);
        assertTrue(htmlBody.contains("Dear John"));
        assertTrue(htmlBody.contains("#1"));
        assertTrue(htmlBody.contains("Test Document"));
        assertTrue(htmlBody.contains("5"));
        assertTrue(htmlBody.contains("A4"));
        assertTrue(htmlBody.contains("Yes")); // For double-sided
        assertTrue(htmlBody.contains("10.50"));
        assertTrue(htmlBody.contains("PrintMatic Copy Center"));
    }

    @Test
    void createHtmlBody_WithSingleSided() {

        OrderEntity order = new OrderEntity();
        order.setId(1L);
        order.setTitle("Test Document");
        order.setCopies(5);
        order.setPageSize(PageSize.A4);
        order.setDoubleSided(false);
        order.setPrice(new BigDecimal("10.50"));

        UserEntity user = new UserEntity();
        user.setFirstName("John");
        user.setLastName("Doe");

        String htmlBody = mailService.createHtmlBody(order, user);

        assertNotNull(htmlBody);
        assertTrue(htmlBody.contains("No")); // For single-sided
    }

    @Test
    void createHtmlBody_ContainsAllRequiredSections() {

        OrderEntity order = new OrderEntity();
        order.setId(1L);
        order.setTitle("Test Document");
        order.setCopies(5);
        order.setPageSize(PageSize.A3);
        order.setDoubleSided(true);
        order.setPrice(new BigDecimal("10.50"));

        UserEntity user = new UserEntity();
        user.setFirstName("John");
        user.setLastName("Doe");


        String htmlBody = mailService.createHtmlBody(order, user);


        assertTrue(htmlBody.contains("<div class=\"header\">"));
        assertTrue(htmlBody.contains("<div class=\"content\">"));
        assertTrue(htmlBody.contains("<div class=\"order-details\">"));
        assertTrue(htmlBody.contains("<div class=\"footer\">"));
        assertTrue(htmlBody.contains("Pickup Information"));
        assertTrue(htmlBody.contains("Monday to Friday"));
    }
}
