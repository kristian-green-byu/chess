package service;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import requests.RegisterRequest;
import responses.RegisterResponse;

public class UserServiceTests {
    AuthDAO authDAO = new MemoryAuthDAO();
    UserService userService = new UserService(authDAO);

    @Test
    public void registerSuccess() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "test@email.com");
        RegisterResponse observedResponse = userService.register(registerRequest);
        String authToken = authDAO.getAuthToken("username");
        RegisterResponse expectedResponse = new RegisterResponse("username", authToken);
        assert observedResponse.equals(expectedResponse);
    }
    @Test
    public void registerFailure() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("username", null, "test@email.com");
        try{
            userService.register(registerRequest);
        } catch (DataAccessException e) {
        assert true;}
    }
}
