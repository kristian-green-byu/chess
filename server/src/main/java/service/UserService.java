package service;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import requests.RegisterRequest;


public class UserService {

    public AuthData register(RegisterRequest registerRequest) throws DataAccessException {
        if(registerRequest == null){
            throw new DataAccessException("register request not found");
        }
        if(registerRequest.email() == null){
            throw new DataAccessException("email not found");
        }
        if(registerRequest.password() == null){
            throw new DataAccessException("password not found");
        }
        if(registerRequest.username() == null){
            throw new DataAccessException("username not found");
        }

    }
    public AuthData login(UserData user){
        return null;
    }
    public void logout(UserData user){}
    public void clearUsers(){
    }
}
