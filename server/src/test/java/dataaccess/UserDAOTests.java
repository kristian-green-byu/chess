package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Test;

public class UserDAOTests {

    @Test
    public void testClearUserData() throws DataAccessException {
        UserDAO userDAO = new SQLUserDAO();
        userDAO.clearUserData();
    }

    @Test
    public void addUserSuccess() throws DataAccessException {
        UserDAO userDAO = new SQLUserDAO();
        UserData user = new UserData("bob", "123", "bob@gmail.com");
        userDAO.createUser(user);
        userDAO.clearUserData();
    }

}
