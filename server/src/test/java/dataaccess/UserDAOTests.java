package dataaccess;

import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class UserDAOTests {

    private static UserDAO userDAO;

    public UserDAOTests() throws DataAccessException{
        userDAO = new SQLUserDAO();
    }
    @AfterEach
    public void clearDB() throws DataAccessException{
        userDAO.clearUserData();
    }

    @Test
    public void testClearUserData() throws DataAccessException {
        userDAO.clearUserData();
    }

    @Test
    public void addUserSuccess() throws DataAccessException {
        UserData user = new UserData("bob", "123", "bob@gmail.com");
        userDAO.createUser(user);
        UserData userObserved = userDAO.getUser("bob");
        assert(userObserved.username().equals(user.username()));
    }

    @Test
    public void addUserAlreadyExists() throws DataAccessException {
        UserData user = new UserData("bob", "123", "bob@gmail.com");
        userDAO.createUser(user);
        UserData user2 =new UserData("bob", "431", "bob@gmail.com");
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.createUser(user2));
    }

    @Test
    public void getUserSuccess() throws DataAccessException {
        UserData user = new UserData("bob", "123", "bob@gmail.com");
        userDAO.createUser(user);
        UserData user2 = new UserData("jill", "145145", "jill@gmail.com");
        userDAO.createUser(user2);
        UserData userObserved = userDAO.getUser("bob");
        assert(userObserved.username().equals(user.username()));
    }

    @Test
    public void getUserWithNull(){
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.getUser(null));
    }
}
