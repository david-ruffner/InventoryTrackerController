package com.davidruffner.inventorytrackercontroller.steps;

import com.davidruffner.inventorytrackercontroller.responseHelpers.ControllerExResponseHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static com.davidruffner.inventorytrackercontroller.ResponseHelpers.getControllerExResponse;

public class BaseProjectSteps extends InventoryControllerBaseTest {
    public static final String APPLICATION_JSON_CHARSET_UTF_8 = "application/json; charset=utf-8";
    public static String BASE_ENDPOINT = "http://localhost:%d%s";
    public static String ALLOWED_IP = "192.168.0.1";
    public static String ALLOWED_IP_HEADER = "X-Forwarded-For";
    public static String USERNAME = "test@test.com";
    public static String SECRET = "5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8"; // Not a real secret, just used for testing purposes.
    public static String FIRST_NAME = "Test";
    public static String LAST_NAME = "Tester";
    public static String PASSWORD = "password"; // Not a real password, only used for testing purposes;
    public static String DEVICE_ID = "fdja8fdaj8";
    public static String DEVICE_NAME = "Kitchen Scanner";

    private ObjectMapper mapper = new ObjectMapper();

    protected RequestBody getRequestBodyFromRequestObj(Object obj) throws JsonProcessingException {
        String jsonStr = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        return RequestBody.Companion.create(jsonStr, MediaType.parse(APPLICATION_JSON_CHARSET_UTF_8));
    }

    protected <T> T doPOSTRequest(Object requestObj, String endpoint,
                                 Map<String, String> headerMap, Class<T> clazz) {
        try {
            OkHttpClient client = new OkHttpClient();
            Headers headers = Headers.of(headerMap);
            RequestBody body = getRequestBodyFromRequestObj(requestObj);

            Request httpRequest = new Request.Builder()
                    .url(super.getEndpointURL(endpoint))
                    .headers(headers)
                    .post(body)
                    .build();

            Response response = client.newCall(httpRequest).execute();
            String responseStr = new String(response.body().bytes(), StandardCharsets.UTF_8);
            return getControllerExResponse(responseStr, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected <T> T doPOSTRequest(Object requestObj, String endpoint, Class<T> clazz) {
        try {
            OkHttpClient client = new OkHttpClient();
            RequestBody body = getRequestBodyFromRequestObj(requestObj);

            Request httpRequest = new Request.Builder()
                    .url(super.getEndpointURL(endpoint))
                    .post(body)
                    .build();

            Response response = client.newCall(httpRequest).execute();
            String responseStr = new String(response.body().bytes(), StandardCharsets.UTF_8);
            return getControllerExResponse(responseStr, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected String getJsonBodyFromException(String exMsg) {
        exMsg = exMsg.replace("<EOL>", ""); // Note this is only an artifact of restTemplate
        return exMsg.substring(exMsg.indexOf('{'), exMsg.indexOf('}') + 1);
    }
}
