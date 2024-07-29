package dataaccess;

import model.UserData;

public interface UserDAO {
    UserData getUser(String username);

    void createUser(UserData user) throws DataAccessException;

    void clearUserData() throws DataAccessException;
}
