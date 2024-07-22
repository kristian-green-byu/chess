package service;
import dataaccess.*;
import org.junit.jupiter.api.Test;
import requests.LoginRequest;
import requests.LogoutRequest;
import requests.RegisterRequest;
import responses.LoginResponse;
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

    @Test
    public void loginSuccess() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("bob", "ross", "painting@email.com");
        userService.register(registerRequest);
        String authToken1 = authDAO.getAuthToken("bob");
        LogoutRequest logoutRequest = new LogoutRequest(authToken1);
        userService.logout(logoutRequest);
        LoginRequest loginRequest = new LoginRequest("bob", "ross");
        LoginResponse observedResponse = userService.login(loginRequest);
        String authToken2 = authDAO.getAuthToken("bob");
        LoginResponse expectedResponse = new LoginResponse("bob", authToken2);
        assert observedResponse.equals(expectedResponse);
    }
    @Test
    public void loginNotRegistered(){
        LoginRequest loginRequest = new LoginRequest("john", "cena");
        try {
            userService.login(loginRequest);
            assert false;
        } catch (DataAccessException ignored) {}
        assert true;
    }
    @Test
    public void loginWithNullField() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "test@email.com");
        userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest(null, "password");
        try{
            userService.login(loginRequest);
            assert false;
        } catch (DataAccessException ignored) {}
        assert true;
    }
}
