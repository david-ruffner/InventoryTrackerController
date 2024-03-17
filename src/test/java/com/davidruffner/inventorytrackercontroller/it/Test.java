package com.davidruffner.inventorytrackercontroller.it;

import com.davidruffner.inventorytrackercontroller.controller.requests.AuthRequest;
import com.davidruffner.inventorytrackercontroller.controller.requests.BaseRequest;
import com.davidruffner.inventorytrackercontroller.controller.responses.AuthResponse;
import com.davidruffner.inventorytrackercontroller.controller.responses.BaseResponse;
import jakarta.persistence.Table;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

public class Test {
    public static String AUTH_ENDPOINT = "http://localhost:8080/auth/token";
    //TODO: Remove
    public static String ALLOWED_IP = "192.168.0.1";
    public static String ALLOWED_IP_HEADER = "X-Forwarded-For";

    @org.junit.jupiter.api.Test
    public void tester() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        AuthRequest jsonRequest = new AuthRequest("davidruffner@icloud.com",
                "password", "fdja8fdaj8");

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(ALLOWED_IP_HEADER, ALLOWED_IP);
        HttpEntity<BaseRequest> request = new HttpEntity<>(jsonRequest, headers);

        String response = restTemplate.postForObject(AUTH_ENDPOINT, request, String.class);
        AuthResponse authResponse = new AuthResponse(response);
    }
}
