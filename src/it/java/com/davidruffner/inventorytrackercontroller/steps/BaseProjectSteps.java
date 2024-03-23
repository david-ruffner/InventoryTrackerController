//package com.davidruffner.inventorytrackercontroller.steps;
//
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.Map;
//
//public class BaseProjectSteps extends InventoryControllerBaseTest {
//    public static String BASE_ENDPOINT = "http://localhost:%d%s";
//    public static String ALLOWED_IP = "192.168.0.1";
//    public static String ALLOWED_IP_HEADER = "X-Forwarded-For";
//    public static String USERNAME = "test@test.com";
//    public static String SECRET = "5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8"; // Not a real secret, just used for testing purposes.
//    public static String FIRST_NAME = "Test";
//    public static String LAST_NAME = "Tester";
//    public static String PASSWORD = "password"; // Not a real password, only used for testing purposes;
//    public static String DEVICE_ID = "fdja8fdaj8";
//    public static String DEVICE_NAME = "Kitchen Scanner";
//
//    private String getEndpointURL(String endpoint) {
//        return String.format(BASE_ENDPOINT, this.serverPort, endpoint);
//    }
//
//    protected ResponseEntity<String> doPOSTRequest(BaseRequest jsonRequest, String endpoint, Map<String, String> headerMap) {
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.setAll(headerMap);
//        HttpEntity<BaseRequest> request = new HttpEntity<>(jsonRequest, headers);
//
//        return restTemplate.postForEntity(getEndpointURL(endpoint), request, String.class);
//    }
//
//    protected String doPOSTRequest(BaseRequest jsonRequest, String endpoint) {
//        RestTemplate restTemplate = new RestTemplate();
//        HttpEntity<BaseRequest> request = new HttpEntity<>(jsonRequest);
//
//        return restTemplate.postForObject(getEndpointURL(endpoint), request, String.class);
//    }
//
//    protected String getJsonBodyFromException(String exMsg) {
//        exMsg = exMsg.replace("<EOL>", ""); // Note this is only an artifact of restTemplate
//        return exMsg.substring(exMsg.indexOf('{'), exMsg.indexOf('}') + 1);
//    }
//}
