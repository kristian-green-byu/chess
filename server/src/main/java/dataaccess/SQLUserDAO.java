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

    public void createUser(UserData user) throws DataAccessException{
        var statement = databaseManager.setDB("INSERT INTO %DB_NAME%.userData (username, password, email) VALUES (?, ?, ?)");
        databaseManager.executeUpdate(statement, user.username(), user.password(), user.email());
    }

    public void clearUserData() throws DataAccessException {
        var statement = databaseManager.setDB("TRUNCATE %DB_NAME%.userData");
        databaseManager.executeUpdate(statement);
    }
}
