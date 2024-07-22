package service;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import requests.LoginRequest;
import requests.LogoutRequest;
import requests.RegisterRequest;
import responses.LoginResponse;
import responses.LogoutResponse;
import responses.RegisterResponse;

public class UserService{

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(AuthDAO authDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public RegisterResponse register(RegisterRequest registerRequest) throws DataAccessException {
        if(registerRequest == null){
            throw new DataAccessException("register request is null");
        }
        else if(registerRequest.email() == null){
            throw new DataAccessException("email is null");
        }
        else if(registerRequest.password() == null){
            throw new DataAccessException("password is null");
        }
        else if(registerRequest.username() == null){
            throw new DataAccessException("username is null");
        }
        String username = registerRequest.username();
        UserData userData = userDAO.getUser(username);
        if(userData != null){
            throw new DataAccessException("username already exists");
        }
        else{
            UserData newUser = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
            userDAO.createUser(newUser);
            String authToken = authDAO.createAuthData(username);
            return new RegisterResponse(username, authToken);
        }
    }

    public LoginResponse login(LoginRequest loginRequest) throws DataAccessException {
        if(loginRequest == null){
            throw new DataAccessException("login request is null");
        }
        else if (loginRequest.password() == null){
            throw new DataAccessException("password is null");
        }
        else if (loginRequest.username() == null){
            throw new DataAccessException("username is null");
        }
        String username = loginRequest.username();
        String password = loginRequest.password();
        UserData userData = userDAO.getUser(username);
        if(userData != null){
            if(!userData.password().equals(password)){
                throw new DataAccessException("password does not match");
            }
            String authToken = authDAO.createAuthData(username);
            return new LoginResponse(username, authToken);
        }
        else {
            throw new DataAccessException("username not registered");
        }
    }

    public LogoutResponse logout(LogoutRequest logoutRequest) throws DataAccessException {
        AuthData authData = authDAO.getAuthData(logoutRequest.authToken());
        if(authData == null){
            throw new DataAccessException("user not logged in");
        }
        else{
            authDAO.deleteAuthData(authData);
            return new LogoutResponse();
        }
    }

    public void clearUsers(){
        authDAO.clearAuthData();
        userDAO.clearUserData();
    }
}
