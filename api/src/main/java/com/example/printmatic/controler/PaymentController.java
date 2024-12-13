package com.example.printmatic.controler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.client.RestTemplate;

@RestController
public class PaymentController {

    @Value("${paypal.client-id}")
    private String clientId;

    @Value("${paypal.client-secret}")
    private String clientSecret;

    private final String PAYPAL_API_URL = "https://api.sandbox.paypal.com";  // За Sandbox среда
    private final RestTemplate restTemplate;

    public PaymentController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    // Потвърждаване на плащането от клиента
    @PostMapping("/api/payment/confirm")
    public String confirmPayment(@RequestBody PaymentConfirmationRequest request) {
        try {
            String orderId = request.getOrderId();

            // Получаване на токен за удостоверяване от PayPal
            String authToken = getPayPalAuthToken();

            // Извличаме информация за поръчката от PayPal
            JsonNode orderDetails = getPayPalOrderDetails(orderId, authToken);

            if (orderDetails != null && orderDetails.path("status").asText().equals("COMPLETED")) {
                // Ако плащането е успешно, връщаме статус
                return "Payment confirmed successfully";
            } else {
                return "Payment failed";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error confirming payment";
        }
    }

    // Метод за получаване на токен от PayPal
    private String getPayPalAuthToken() {
        // Създаване на заявка за получаване на токен
        String url = PAYPAL_API_URL + "/v1/oauth2/token";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + encodeCredentials(clientId, clientSecret));
        headers.set("Content-Type", "application/x-www-form-urlencoded");

        String body = "grant_type=client_credentials";
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.POST, request, JsonNode.class);
        JsonNode responseBody = response.getBody();

        return responseBody != null ? responseBody.path("access_token").asText() : null;
    }

    // Метод за получаване на информация за поръчката от PayPal
    private JsonNode getPayPalOrderDetails(String orderId, String authToken) {
        String url = PAYPAL_API_URL + "/v2/checkout/orders/" + orderId;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + authToken);

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, request, JsonNode.class);

        return response.getBody();
    }

    // Метод за кодиране на клиентски идентификатор и секрет в Base64
    private String encodeCredentials(String clientId, String clientSecret) {
        return java.util.Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
    }
}

// DTO клас за обработка на потвърждението
class PaymentConfirmationRequest {
    private String orderId;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
