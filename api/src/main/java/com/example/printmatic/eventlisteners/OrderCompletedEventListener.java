package com.example.printmatic.eventlisteners;

import com.example.printmatic.dto.EmailContentEvent;
import com.example.printmatic.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Async
@Slf4j
public class OrderCompletedEventListener {
    private final MailService mailService;

    public OrderCompletedEventListener(MailService mailService) {
        this.mailService = mailService;
    }

    @EventListener
    public void handleOrderCompletedEvent(EmailContentEvent emailContentDTO) {
        try {
            mailService.sendEmail(
                    emailContentDTO.getTo(),
                    emailContentDTO.getSubject(),
                    emailContentDTO.getBody()
            );
        } catch (Exception e) {
            log.error("Failed to send completion email for the user {}: {}",
                    emailContentDTO.getTo(), e.getMessage());
        }
    }
}
