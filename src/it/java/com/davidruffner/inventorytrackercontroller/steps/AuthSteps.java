package com.davidruffner.inventorytrackercontroller.steps;

import com.davidruffner.inventorytrackercontroller.controller.requests.AuthRequest;
import com.davidruffner.inventorytrackercontroller.controller.responses.AuthResponse;
import com.davidruffner.inventorytrackercontroller.controller.responses.ResponseStatus;
import com.davidruffner.inventorytrackercontroller.controller.responses.ResponseStatus.ResponseStatusCode;
import com.davidruffner.inventorytrackercontroller.db.entities.Device;
import com.davidruffner.inventorytrackercontroller.db.entities.User;
import com.davidruffner.inventorytrackercontroller.responseHelpers.AuthResponseHelper;
import com.davidruffner.inventorytrackercontroller.responseHelpers.ControllerExResponseHelper;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.junit.Cucumber;
import org.junit.runner.RunWith;

import java.util.Map;

import static com.davidruffner.inventorytrackercontroller.controller.responses.ResponseStatus.ResponseStatusCode.*;
import static com.davidruffner.inventorytrackercontroller.controller.responses.ResponseStatus.getHttpStatusIntFromResponseStatus;
import static com.davidruffner.inventorytrackercontroller.util.Constants.AUTH_RESPONSE_BUILDER_BEAN;
import static com.davidruffner.inventorytrackercontroller.util.Constants.GENERAL_RESPONSE_BUILDER_BEAN;
import static org.junit.jupiter.api.Assertions.*;


@RunWith(Cucumber.class)
public class AuthSteps extends BaseProjectSteps {
    private static final String TOKEN_ENDPOINT = "/auth/token";
    private ControllerExResponseHelper controllerExResponseHelper;
    private AuthResponseHelper authResponseHelper;

    @Before
    public void setUp() throws RuntimeException {
        Device device = new Device(DEVICE_ID, DEVICE_NAME);
        this.deviceRepo.save(device);

        User user = new User(USERNAME, SECRET, FIRST_NAME, LAST_NAME, true);
        user.addAuthorizedDevice(device);
        this.userRepo.save(user);
    }

    @When("the client calls token with valid username and password")
    public void clientCallsTokenValid() {
        AuthRequest request = new AuthRequest(USERNAME, PASSWORD, DEVICE_ID);
        this.authResponseHelper = doPOSTRequest(request, TOKEN_ENDPOINT,
                Map.ofEntries(Map.entry(ALLOWED_IP_HEADER, ALLOWED_IP)),
                AuthResponseHelper.class);
    }

    @Then("the client receives a valid token back")
    public void clientReceivesValidToken() {
        assertFalse(this.authResponseHelper.getToken().isEmpty());
        assertFalse(this.authResponseHelper.getDisplayName().isEmpty());
    }

    @When("the client calls token with unauthorized IP address")
    public void clientCallsTokenUnauthorizedIP() {
        AuthRequest request = new AuthRequest(USERNAME, PASSWORD, DEVICE_ID);
        this.controllerExResponseHelper = doPOSTRequest(request, TOKEN_ENDPOINT,
                Map.ofEntries(Map.entry(ALLOWED_IP_HEADER, "127.0.0.1")),
                ControllerExResponseHelper.class);
    }

    @Then("the client receives NOT_A_CHANCE status code and 418 HTTP code")
    public void theClientReceivesNOT_A_CHANCEStatusCodeAndHTTPCode() {
        ResponseStatusCode responseStatus = controllerExResponseHelper.getResponseStatus();
        assertEquals(NOT_A_CHANCE, responseStatus);
        assertEquals(418, getHttpStatusIntFromResponseStatus(responseStatus));
        assertFalse(controllerExResponseHelper.getMessage().isEmpty());
    }

    @When("the client calls token with invalid IPv4 address")
    public void theClientCallsTokenWithInvalidIPvAddress() throws Exception {
        AuthRequest request = new AuthRequest(USERNAME, PASSWORD, DEVICE_ID);
        this.controllerExResponseHelper = doPOSTRequest(request, TOKEN_ENDPOINT,
                Map.ofEntries(Map.entry(ALLOWED_IP_HEADER, "fdajfaf")),
                ControllerExResponseHelper.class);
    }

    @Then("the client receives a bad request response")
    public void theClientReceivesABadRequestResponse() {
        assertEquals(BAD_REQUEST, controllerExResponseHelper.getResponseStatus());
        assertFalse(controllerExResponseHelper.getMessage().isEmpty());
    }


    @When("the client calls token with invalid username")
    public void theClientCallsTokenWithInvalidUsername() {
        AuthRequest request = new AuthRequest("invalidUsername", PASSWORD, DEVICE_ID);
        this.controllerExResponseHelper = doPOSTRequest(request, TOKEN_ENDPOINT,
                Map.ofEntries(Map.entry(ALLOWED_IP_HEADER, ALLOWED_IP)),
                ControllerExResponseHelper.class);
    }

    @When("the client calls token with invalid password")
    public void theClientCallsTokenWithInvalidPassword() {
        AuthRequest request = new AuthRequest(USERNAME, "invalidPassword", DEVICE_ID);
        this.controllerExResponseHelper = doPOSTRequest(request, TOKEN_ENDPOINT,
                Map.ofEntries(Map.entry(ALLOWED_IP_HEADER, ALLOWED_IP)),
                ControllerExResponseHelper.class);
    }

    @Then("the client receives USER_NOT_AUTHORIZED message back")
    public void theClientReceivesUNAUTHORIZEDMessageBack() {
        assertEquals(USER_NOT_AUTHORIZED, controllerExResponseHelper.getResponseStatus());
        assertFalse(controllerExResponseHelper.getMessage().isEmpty());
    }
}
