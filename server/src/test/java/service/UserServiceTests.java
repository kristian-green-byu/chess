package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import org.junit.jupiter.api.Test;
import requests.RegisterRequest;
import responses.RegisterResponse;
import service.UserService;

public class UserServiceTests {
    @Test
    public void registerSuccess() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "test@email.com");
        AuthDAO authDAO = new MemoryAuthDAO();
        UserService userService = new UserService(authDAO);
        RegisterResponse observedResponse = userService.register(registerRequest);
        String authToken = authDAO.getAuthToken("username");
        RegisterResponse expectedResponse = new RegisterResponse("username", authToken);
        assert observedResponse.equals(expectedResponse);
    }
}
