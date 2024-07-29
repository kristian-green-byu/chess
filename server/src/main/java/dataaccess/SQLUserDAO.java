package dataaccess;

import model.UserData;

public class SQLUserDAO implements UserDAO{

    private final DatabaseManager databaseManager;

    public SQLUserDAO() throws DataAccessException{
        this.databaseManager = new DatabaseManager();
    }

    public UserData getUser(String username){
        return null;
    }

    public void createUser(UserData user){

    }

    public void clearUserData() throws DataAccessException {
        var statement = databaseManager.setDB("TRUNCATE %DB_NAME%.userData");
        databaseManager.executeUpdate(statement);
    }
}
