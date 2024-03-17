package com.davidruffner.inventorytrackercontroller.steps;

import com.davidruffner.inventorytrackercontroller.controller.requests.AuthRequest;
import com.davidruffner.inventorytrackercontroller.controller.responses.AuthResponse;
import com.davidruffner.inventorytrackercontroller.controller.responses.BadRequestResponse;
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

import static com.davidruffner.inventorytrackercontroller.controller.responses.AuthResponse.AuthStatus.NOT_A_CHANCE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


@RunWith(Cucumber.class)
public class AuthSteps extends BaseProjectSteps {
    //TODO: Need to return ResponseEntities for all responses

    private static String TOKEN_ENDPOINT = "/auth/token";
    private AuthResponse authResponse;
    private BadRequestResponse badRequestResponse;

    @Before
    public void setUp() throws AuthException {
        AllowedIPAddress ipAddress = new AllowedIPAddress(ALLOWED_IP);
        this.allowedIPAddressRepo.save(ipAddress);

        Device device = new Device(DEVICE_ID, DEVICE_NAME);
        this.deviceRepo.save(device);

        User user = new User(USERNAME, SECRET, FIRST_NAME, LAST_NAME, true);
        user.addAuthorizedDevice(device);
        this.userRepo.save(user);
    }

    @When("the client calls token with valid username and password")
    public void clientCallsTokenValid() throws Exception {
        AuthRequest request = new AuthRequest(USERNAME, PASSWORD, DEVICE_ID);
        authResponse = new AuthResponse(doPOSTRequest(request, TOKEN_ENDPOINT,
                Map.ofEntries(Map.entry(ALLOWED_IP_HEADER, ALLOWED_IP))));
    }

    @Then("the client receives a valid token back")
    public void clientReceivesValidToken() {
        assertFalse(authResponse.getToken().isEmpty());
    }

    @When("the client calls token with unauthorized IP address")
    public void clientCallsTokenUnauthorizedIP() throws Exception {
        AuthRequest request = new AuthRequest(USERNAME, PASSWORD, DEVICE_ID);
        try {
            authResponse = new AuthResponse(doPOSTRequest(request, TOKEN_ENDPOINT,
                    Map.ofEntries(Map.entry(ALLOWED_IP_HEADER, "127.0.0.1"))));
        } catch (Exception ex) {
            authResponse = new AuthResponse(getJsonBodyFromException(ex.getMessage()));
        }
    }

    @Then("the client receives NOT_A_CHANCE status code and 418 HTTP code")
    public void theClientReceivesNOT_A_CHANCEStatusCodeAndHTTPCode() {
        assertEquals(NOT_A_CHANCE, authResponse.getAuthStatus());
        assertEquals(418, authResponse.getHttpStatusInt());
    }

    @When("the client calls token with invalid IPv4 address")
    public void theClientCallsTokenWithInvalidIPvAddress() throws Exception {
        AuthRequest request = new AuthRequest(USERNAME, PASSWORD, DEVICE_ID);
        try {
            badRequestResponse = new BadRequestResponse(doPOSTRequest(request, TOKEN_ENDPOINT,
                    Map.ofEntries(Map.entry(ALLOWED_IP_HEADER, "blahblah"))));
        } catch (Exception ex) {
            badRequestResponse = new BadRequestResponse(getJsonBodyFromException(ex.getMessage()));
        }
    }

    @Then("the client receives a bad request response")
    public void theClientReceivesABadRequestResponse() {
        System.out.println("");
    }
}
