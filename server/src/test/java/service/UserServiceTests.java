package service;
import dataaccess.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import requests.LoginRequest;
import requests.LogoutRequest;
import requests.RegisterRequest;
import responses.LoginResponse;
import responses.LogoutResponse;
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
        Assertions.assertThrows(DataAccessException.class, () -> userService.register(registerRequest));
    }

    @Test
    public void registerAlreadyRegistered() throws DataAccessException {
        RegisterRequest registerRequest1 = new RegisterRequest("username", "password", "test@email.com");
        userService.register(registerRequest1);
        RegisterRequest registerRequest2 = new RegisterRequest("username", "password2", "test2@email.com");
        Assertions.assertThrows(DataAccessException.class, () -> userService.register(registerRequest2));
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
        Assertions.assertThrows(DataAccessException.class, () -> userService.login(loginRequest));
    }

    @Test
    public void loginWithNullField() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "test@email.com");
        userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest(null, "password");
        Assertions.assertThrows(DataAccessException.class, () -> userService.login(loginRequest));
    }

    @Test
    public void logoutSuccess() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("john", "doe", "jd@email.com");
        RegisterResponse registerResponse = userService.register(registerRequest);
        String authToken = registerResponse.authToken();
        LogoutRequest logoutRequest = new LogoutRequest(authToken);
        LogoutResponse logoutResponse = userService.logout(logoutRequest);
        assert logoutResponse != null && authDAO.getAuthData(authToken)==null;
    }

    @Test
    public void logoutNotLoggedIn() throws DataAccessException {
        LogoutRequest logoutRequest = new LogoutRequest("authToken");
        Assertions.assertThrows(DataAccessException.class, () -> userService.logout(logoutRequest));
    }
}
