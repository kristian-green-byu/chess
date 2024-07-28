package dataaccess;

import model.UserData;

public class SQLUserDAO implements UserDAO{

    public SQLUserDAO() throws DataAccessException{
        DatabaseManager.configureDatabase();
    }

    public UserData getUser(String username){
        return null;
    }

    public void createUser(UserData user){

    }

    public void clearUserData(){

    }
}
