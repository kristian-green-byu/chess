package dataaccess;

import model.AuthData;

public class SQLAuthDAO implements AuthDAO{

    public SQLAuthDAO() throws DataAccessException{
        DatabaseManager.configureDatabase();
    }

    public String createAuthData(String username){
        return null;
    }

    public String getAuthToken(String username){
        return null;
    }

    public AuthData getAuthData(String authToken){
        return null;
    }

    public void clearAuthData(){
    }

    public void deleteAuthData(AuthData authData){
    }
}
