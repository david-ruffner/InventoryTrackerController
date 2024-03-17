package com.davidruffner.inventorytrackercontroller.steps;

import com.davidruffner.inventorytrackercontroller.controller.requests.AuthRequest;
import com.davidruffner.inventorytrackercontroller.controller.responses.AuthResponse;
import com.davidruffner.inventorytrackercontroller.controller.responses.GeneralResponse;
import com.davidruffner.inventorytrackercontroller.db.entities.AllowedIPAddress;
import com.davidruffner.inventorytrackercontroller.db.entities.Device;
import com.davidruffner.inventorytrackercontroller.db.entities.User;
import com.davidruffner.inventorytrackercontroller.exceptions.AuthException;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.junit.Cucumber;
import org.junit.runner.RunWith;

import java.util.Map;

import static com.davidruffner.inventorytrackercontroller.controller.responses.ResponseStatus.ResponseStatusCode.BAD_REQUEST;
import static com.davidruffner.inventorytrackercontroller.controller.responses.ResponseStatus.ResponseStatusCode.NOT_A_CHANCE;
import static com.davidruffner.inventorytrackercontroller.util.Constants.AUTH_RESPONSE_BUILDER_BEAN;
import static com.davidruffner.inventorytrackercontroller.util.Constants.GENERAL_RESPONSE_BUILDER_BEAN;
import static org.junit.jupiter.api.Assertions.*;


@RunWith(Cucumber.class)
public class AuthSteps extends BaseProjectSteps {
    private static String TOKEN_ENDPOINT = "/auth/token";
    private AuthResponse.Builder authResponseBuilder;
    private GeneralResponse.Builder generalResponseBuilder;
    private AuthResponse authResponse;
    private GeneralResponse badRequestResponse;

    @Before
    public void setUp() throws AuthException {
        AllowedIPAddress ipAddress = new AllowedIPAddress(ALLOWED_IP);
        this.allowedIPAddressRepo.save(ipAddress);

        Device device = new Device(DEVICE_ID, DEVICE_NAME);
        this.deviceRepo.save(device);

        User user = new User(USERNAME, SECRET, FIRST_NAME, LAST_NAME, true);
        user.addAuthorizedDevice(device);
        this.userRepo.save(user);

        authResponseBuilder = (AuthResponse.Builder) appContext.getBean(AUTH_RESPONSE_BUILDER_BEAN);
        generalResponseBuilder = (GeneralResponse.Builder) appContext.getBean(GENERAL_RESPONSE_BUILDER_BEAN);
    }

    @When("the client calls token with valid username and password")
    public void clientCallsTokenValid() throws Exception {
        AuthRequest request = new AuthRequest(USERNAME, PASSWORD, DEVICE_ID);
        authResponse = authResponseBuilder.buildResponseFromJSON(doPOSTRequest(request, TOKEN_ENDPOINT,
                Map.ofEntries(Map.entry(ALLOWED_IP_HEADER, ALLOWED_IP))).getBody());
    }

    @Then("the client receives a valid token back")
    public void clientReceivesValidToken() {
        assertFalse(authResponse.getToken().isEmpty());
    }

    @When("the client calls token with unauthorized IP address")
    public void clientCallsTokenUnauthorizedIP() throws Exception {
        AuthRequest request = new AuthRequest(USERNAME, PASSWORD, DEVICE_ID);

        try {
            doPOSTRequest(request, TOKEN_ENDPOINT, Map.ofEntries(Map.entry(ALLOWED_IP_HEADER, "127.0.0.1")));
        } catch (Exception ex) {
            String responseBody = getJsonBodyFromException(ex.getMessage());
            authResponse = authResponseBuilder.buildResponseFromJSON(responseBody);
        }
    }

    @Then("the client receives NOT_A_CHANCE status code and 418 HTTP code")
    public void theClientReceivesNOT_A_CHANCEStatusCodeAndHTTPCode() {
        assertEquals(NOT_A_CHANCE, authResponse.getResponseStatus());
        assertEquals(418, authResponse.getHttpStatusInt());
    }

    @When("the client calls token with invalid IPv4 address")
    public void theClientCallsTokenWithInvalidIPvAddress() throws Exception {
        AuthRequest request = new AuthRequest(USERNAME, PASSWORD, DEVICE_ID);

        try {
            doPOSTRequest(request, TOKEN_ENDPOINT, Map.ofEntries(Map.entry(ALLOWED_IP_HEADER, "fdj8af")));
        } catch (Exception ex) {
            String responseBody = getJsonBodyFromException(ex.getMessage());
            badRequestResponse = (GeneralResponse) generalResponseBuilder.buildResponseFromJSON(responseBody);
        }
    }

    @Then("the client receives a bad request response")
    public void theClientReceivesABadRequestResponse() {
        assertEquals(BAD_REQUEST, badRequestResponse.getResponseStatus());
        assertTrue(badRequestResponse.getMessage().isPresent());
    }
}
