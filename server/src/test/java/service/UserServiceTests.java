package service;
import dataaccess.*;
import org.junit.jupiter.api.Test;
import requests.RegisterRequest;
import responses.RegisterResponse;

public class UserServiceTests {
    AuthDAO authDAO = new MemoryAuthDAO();
    UserDAO userDAO = new MemoryUserDAO();
    UserService userService = new UserService(authDAO, userDAO);

    @Test
    public void registerSuccess() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "test@email.com");
        RegisterResponse observedResponse = userService.register(registerRequest);
        String authToken = authDAO.getAuthToken("username");
        RegisterResponse expectedResponse = new RegisterResponse("username", authToken);
        assert observedResponse.equals(expectedResponse);
    }
    @Test
    public void registerWithNullField(){
        RegisterRequest registerRequest = new RegisterRequest("username", null, "test@email.com");
        try{
            userService.register(registerRequest);
            assert false;
        } catch (DataAccessException ignored) {}
        assert true;
    }
    @Test
    public void registerAlreadyRegistered() throws DataAccessException {
        RegisterRequest registerRequest1 = new RegisterRequest("username", "password", "test@email.com");
        userService.register(registerRequest1);
        RegisterRequest registerRequest2 = new RegisterRequest("username", "password2", "test2@email.com");
        try{
            userService.register(registerRequest2);
            assert false;
        } catch (DataAccessException ignored) {}
        assert true;
    }
}
