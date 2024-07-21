package service;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import requests.RegisterRequest;
import responses.RegisterResponse;

public class UserService {

    private final dataaccess.AuthDAO authDAO;
    public UserService(AuthDAO authDAO) {
        this.authDAO = authDAO;
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
        String authToken = authDAO.createAuthData(username);
        return new RegisterResponse(username, authToken);
    }
    public AuthData login(UserData user){
        return null;
    }
    public void logout(UserData user){}
    public void clearUsers(){
    }
}
