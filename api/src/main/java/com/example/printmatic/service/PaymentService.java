package com.example.printmatic.service;

import com.example.printmatic.dto.request.OrderPaymentSuccessDTO;
import com.example.printmatic.dto.response.MessageResponseDTO;
import com.example.printmatic.dto.response.SessionResponseDTO;
import com.example.printmatic.enums.OrderStatus;
import com.example.printmatic.enums.PaymentType;
import com.example.printmatic.model.OrderEntity;
import com.example.printmatic.model.PaymentEntity;
import com.example.printmatic.model.UserEntity;
import com.example.printmatic.repository.OrderRepository;
import com.example.printmatic.repository.PaymentRepository;
import com.example.printmatic.repository.UserRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.persistence.LockModeType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class PaymentService {
    private Stripe stripe;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @Value("${STRIPE_SECRET_KEY}")
    private String stripeKey;

    public PaymentService(UserRepository userRepository, OrderRepository orderRepository, PaymentRepository paymentRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
    }

    @PostConstruct
    public void init(){
        stripe.apiKey = stripeKey;
    }

    public SessionResponseDTO createSessionForPayingOrder(Long orderId, Principal principal) {
        try {
            OrderEntity orderEntity = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Order not found"));

            UserEntity user = userRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            Long amountInCents = orderEntity.getPrice()
                    .setScale(2, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"))
                    .longValue();

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setCustomerEmail(user.getEmail())
                    .setSuccessUrl("http://localhost5173:/order/" + orderId + "/success")  // Consider making these URLs configurable
                    .setCancelUrl("http://localhost:5173/order/" + orderId + "/fail")
                    .addLineItem(SessionCreateParams.LineItem.builder()
                            .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency("bgn")
                                    .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                            .setName("Order Payment")
                                            .setDescription("Payment for order " + orderId)
                                            .build())
                                    .setUnitAmount(amountInCents)
                                    .build())
                            .setQuantity(1L)
                            .build())
                    .build();
            Session session = Session.create(params);

            SessionResponseDTO sessionResponseDTO = new SessionResponseDTO();
            sessionResponseDTO.setSessionId(session.getId());
            sessionResponseDTO.setStripePaymentURL(session.getUrl());

            return sessionResponseDTO;
        } catch (StripeException e) {
            log.error("Error creating Stripe session: ", e);
            throw new RuntimeException("Failed to create payment session", e);
        }
    }

    @Transactional
    public MessageResponseDTO orderSuccess(OrderPaymentSuccessDTO orderPaymentSuccessDTO, Principal principal) {
        try {
            Session session = Session.retrieve(orderPaymentSuccessDTO.getStripeSessionId());
            PaymentIntent paymentIntent = PaymentIntent.retrieve(session.getPaymentIntent());
            Charge charge = Charge.retrieve(paymentIntent.getLatestCharge());

            if (charge.getStatus().equals("succeeded")) {
                OrderEntity orderEntity = orderRepository.findById(orderPaymentSuccessDTO.getOrderId()).orElseThrow(() -> new IllegalArgumentException("Order not found"));
                UserEntity userEntity = userRepository.findByEmail(principal.getName()).orElseThrow(() -> new IllegalArgumentException("User not found"));

                PaymentEntity paymentEntity = new PaymentEntity();
                paymentEntity.setPaymentType(PaymentType.STRIPE);
                paymentEntity.setPaidAt(LocalDateTime.now());
                paymentEntity.setStripeSessionId(orderPaymentSuccessDTO.getStripeSessionId());
                paymentEntity.setAmount(orderEntity.getPrice());

                paymentEntity.setOrder(orderEntity);
                paymentEntity.setUser(userEntity);

                paymentRepository.save(paymentEntity);

                userEntity.getPayments().add(paymentEntity);
                orderEntity.setStatus(OrderStatus.PENDING);
                orderEntity.setPayment(paymentEntity);
                orderRepository.save(orderEntity);
                userRepository.save(userEntity);

                return new MessageResponseDTO(200, "Payment successful");
            }else {
                return new MessageResponseDTO(500, "Charge not successful");
            }
        } catch (StripeException e) {
            return new MessageResponseDTO(500, "Stripe session could not be retrieved");
        }
    }

    @Transactional
    public MessageResponseDTO depositBalanceSuccess(String stripeId, Principal principal) {
        try {
            Session session = Session.retrieve(stripeId);
            PaymentIntent paymentIntent = PaymentIntent.retrieve(session.getPaymentIntent());
            Charge charge = Charge.retrieve(paymentIntent.getLatestCharge());

            Optional<PaymentEntity> payment = paymentRepository.findByStripeSessionId(stripeId);

            if (charge.getStatus().equals("succeeded") && payment.isEmpty()) {
//                savePaymentTransactional(charge, principal, stripeId);

                UserEntity user = userRepository.findByEmail(principal.getName())
                        .orElseThrow(() -> new IllegalArgumentException("User not found"));

                BigDecimal amount = BigDecimal.valueOf(charge.getAmount())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

                PaymentEntity paymentEntity = new PaymentEntity();
                paymentEntity.setPaymentType(PaymentType.STRIPE);
                paymentEntity.setPaidAt(LocalDateTime.now());
                paymentEntity.setAmount(amount);
                paymentEntity.setStripeSessionId(stripeId);

                paymentEntity.setUser(user);

                paymentRepository.save(paymentEntity);

                user.setBalance(user.getBalance().add(amount));
                user.getPayments().add(paymentEntity);
                userRepository.save(user);


                return new MessageResponseDTO(200, "Deposit balance successful");
            }else {
                log.error("session cancelled");
                return new MessageResponseDTO(500, "Charge not successful");
            }
        } catch (StripeException e) {
            return new MessageResponseDTO(500, "Stripe session could not be retrieved");
        }
    }

//    @Transactional
//    public void savePaymentTransactional(Charge charge, Principal principal, String stripeId) {
//    }


    public SessionResponseDTO addToBalanceSession(BigDecimal amount, Principal principal) {
        try {
            UserEntity user = userRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            Long amountInCents = amount
                    .setScale(2, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"))
                    .longValue();

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setCustomerEmail(user.getEmail())
                    .setSuccessUrl("http://localhost:5173/balancePayment")  // Consider making these URLs configurable
                    .setCancelUrl("http://localhost:5173/userProfile")
                    .addLineItem(SessionCreateParams.LineItem.builder()
                            .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency("bgn")
                                    .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                            .setName("Deposit balance")
                                            .setDescription("Deposit balance for user " + user.getFirstName() + " " + user.getLastName())
                                            .build())
                                    .setUnitAmount(amountInCents)
                                    .build())
                            .setQuantity(1L)
                            .build())
                    .build();
            Session session = Session.create(params);

            SessionResponseDTO sessionResponseDTO = new SessionResponseDTO();
            sessionResponseDTO.setSessionId(session.getId());
            sessionResponseDTO.setStripePaymentURL(session.getUrl());

            return sessionResponseDTO;
        } catch (StripeException e) {
            log.error("Error creating Stripe session: ", e);
            throw new RuntimeException("Failed to create payment session", e);
        }
    }

    @Transactional
    public MessageResponseDTO payFromBalance(Long orderId, Principal principal) {
        OrderEntity orderEntity = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        UserEntity userEntity = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if(userEntity.getBalance().compareTo(orderEntity.getPrice()) < 0) {
            return new MessageResponseDTO(500, "Payment not successful");
        }

        PaymentEntity paymentEntity = new PaymentEntity();
        paymentEntity.setPaymentType(PaymentType.BALANCE);
        paymentEntity.setPaidAt(LocalDateTime.now());
        paymentEntity.setAmount(orderEntity.getPrice());
        paymentEntity.setStripeSessionId(null);

        paymentEntity.setOrder(orderEntity);
        paymentEntity.setUser(userEntity);
        paymentRepository.save(paymentEntity);

        userEntity.setBalance(userEntity.getBalance().subtract(orderEntity.getPrice()));
        userEntity.getPayments().add(paymentEntity);
        orderEntity.setStatus(OrderStatus.PENDING);
        orderEntity.setPayment(paymentEntity);
        orderRepository.save(orderEntity);
        userRepository.save(userEntity);

        return new MessageResponseDTO(200, "Payment from balance successful");

    }
}
