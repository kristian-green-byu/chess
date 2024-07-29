package dataaccess;

import org.junit.jupiter.api.Test;

public class UserDAOTests {

    @Test
    public void testclearUserData() throws DataAccessException {
        UserDAO userDAO = new SQLUserDAO();
        userDAO.clearUserData();
    }

}
