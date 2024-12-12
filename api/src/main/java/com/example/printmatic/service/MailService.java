package com.example.printmatic.service;

import com.example.printmatic.model.OrderEntity;
import com.example.printmatic.model.UserEntity;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String host;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


    public void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(host);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
    public String createHtmlBody(OrderEntity order, UserEntity user) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { 
                        font-family: Arial, sans-serif;
                        line-height: 1.6;
                        color: #333;
                        max-width: 600px;
                        margin: 0 auto;
                    }
                    .header {
                        background-color: #4CAF50;
                        color: white;
                        padding: 20px;
                        text-align: center;
                    }
                    .content {
                        padding: 20px;
                    }
                    .order-details {
                        background-color: #f9f9f9;
                        padding: 15px;
                        border-radius: 5px;
                        margin: 20px 0;
                    }
                    .footer {
                        text-align: center;
                        padding: 20px;
                        background-color: #f1f1f1;
                        font-size: 0.9em;
                    }
                </style>
            </head>
            <body>
                <div class="header">
                    <h1>Your Order is Ready!</h1>
                </div>
                
                <div class="content">
                    <p>Dear %s,</p>
                    
                    <p>Great news! Your order is ready for pickup at PrintMatic Copy Center.</p>
                    
                    <div class="order-details">
                        <h2>Order Details:</h2>
                        <p><strong>Order Number:</strong> #%d</p>
                        <p><strong>Title:</strong> %s</p>
                        <p><strong>Copies:</strong> %d</p>
                        <p><strong>Paper Size:</strong> %s</p>
                        <p><strong>Double-sided:</strong> %s</p>
                        <p><strong>Total Price:</strong> $%.2f</p>
                    </div>
                    
                    <h3>Pickup Information</h3>
                    <p>You can collect your order during our business hours:</p>
                    <p><strong>Monday to Friday, 8:00 AM - 5:00 PM</strong></p>
                    
                    <p>If you have any questions, feel free to contact us.</p>
                    
                    <p>Thank you for choosing PrintMatic Copy Center!</p>
                    
                    <p>Best regards,<br>
                    PrintMatic Copy Center Team</p>
                </div>
                
                <div class="footer">
                    <p>PrintMatic Copy Center | Your Trusted Printing Partner</p>
                </div>
            </body>
            </html>
            """,
                user.getFirstName(),
                order.getId(),
                order.getTitle(),
                order.getCopies(),
                order.getPageSize(),
                order.isDoubleSided() ? "Yes" : "No",
                order.getPrice()
        );
    }
}
