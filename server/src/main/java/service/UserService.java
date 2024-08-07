package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import requests.LoginRequest;
import requests.LogoutRequest;
import requests.RegisterRequest;
import responses.LoginResponse;
import responses.LogoutResponse;
import responses.RegisterResponse;

public class UserService {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(AuthDAO authDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public RegisterResponse register(RegisterRequest registerRequest) throws DataAccessException {
        if (registerRequest == null) {
            throw new DataAccessException("bad request");
        } else if (registerRequest.email() == null) {
            throw new DataAccessException("bad request");
        } else if (registerRequest.password() == null) {
            throw new DataAccessException("bad request");
        } else if (registerRequest.username() == null) {
            throw new DataAccessException("bad request");
        }
        String username = registerRequest.username();
        UserData userData = userDAO.getUser(username);
        if (userData != null) {
            throw new DataAccessException("already taken");
        } else {
            UserData newUser = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
            userDAO.createUser(newUser);
            String authToken = authDAO.createAuthData(username);
            return new RegisterResponse(username, authToken);
        }
    }

    public LoginResponse login(LoginRequest loginRequest) throws DataAccessException {
        if (loginRequest == null) {
            throw new DataAccessException("unauthorized");
        } else if (loginRequest.password() == null) {
            throw new DataAccessException("unauthorized");
        } else if (loginRequest.username() == null) {
            throw new DataAccessException("unauthorized");
        }
        String username = loginRequest.username();
        String password = loginRequest.password();

        UserData userData = userDAO.getUser(username);
        if (userData != null) {
            if(!BCrypt.checkpw(password, userData.password())){
                throw new DataAccessException("unauthorized");
            }
            String authToken = authDAO.createAuthData(username);
            return new LoginResponse(username, authToken);
        } else {
            throw new DataAccessException("unauthorized");
        }
    }

    public LogoutResponse logout(LogoutRequest logoutRequest) throws DataAccessException {
        AuthData authData = authDAO.getAuthData(logoutRequest.authToken());
        if (authData == null) {
            throw new DataAccessException("unauthorized");
        } else {
            authDAO.deleteAuthData(authData);
            return new LogoutResponse();
        }
    }
}
