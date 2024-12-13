package com.example.printmatic.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {

    @Value("${paypal.client-id}")
    private String clientId;

    @Value("${paypal.secret}")
    private String secret;

    private static final String PAYPAL_API = "https://api.sandbox.paypal.com"; // sandbox URL

    @PostMapping("/create-payment")
    public ResponseEntity<Map<String, String>> createPayment(@RequestBody Map<String, Object> body) {
        double price = (double) body.get("price");

        Map<String, Object> paymentRequest = new HashMap<>();
        paymentRequest.put("intent", "sale");
        paymentRequest.put("payer", Map.of("payment_method", "paypal"));

        Map<String, Object> transaction = new HashMap<>();
        transaction.put("amount", Map.of("total", String.valueOf(price), "currency", "USD"));
        transaction.put("description", "Payment for order");

        Map<String, String> redirectUrls = new HashMap<>();
        redirectUrls.put("cancel_url", "https://www.example.com/cancel");
        redirectUrls.put("return_url", "https://www.example.com/execute");
        paymentRequest.put("redirect_urls", redirectUrls);

        paymentRequest.put("transactions", new Map[]{transaction});

        RestTemplate restTemplate = new RestTemplate();
        String accessToken = getAccessToken();

        String url = PAYPAL_API + "/v1/payments/payment";
        ResponseEntity<Map> response = restTemplate.postForEntity(url, paymentRequest, Map.class);

        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("id", (String) response.getBody().get("id"));  // Връща ID на плащането

        return ResponseEntity.ok(responseMap);
    }

    @PostMapping("/execute-payment")
    public ResponseEntity<String> executePayment(@RequestBody Map<String, String> body) {
        String paymentId = body.get("paymentID");
        String payerId = body.get("payerID");

        String url = PAYPAL_API + "/v1/payments/payment/" + paymentId + "/execute";
        Map<String, Object> executeRequest = new HashMap<>();
        executeRequest.put("payer_id", payerId);

        RestTemplate restTemplate = new RestTemplate();
        String accessToken = getAccessToken();
        restTemplate.postForEntity(url, executeRequest, String.class);

        return ResponseEntity.ok("Payment executed successfully");
    }

    private String getAccessToken() {
        String url = PAYPAL_API + "/v1/oauth2/token";
        String auth = "Basic " + java.util.Base64.getEncoder().encodeToString((clientId + ":" + secret).getBytes());

        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", auth);

        Map<String, String> body = new HashMap<>();
        body.put("grant_type", "client_credentials");

        ResponseEntity<Map> response = restTemplate.postForEntity(url, body, Map.class);
        return (String) response.getBody().get("access_token");
    }
}
