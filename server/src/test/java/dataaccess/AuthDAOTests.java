package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AuthDAOTests {
    private static AuthDAO authDAO;

    public AuthDAOTests() throws DataAccessException{
        authDAO = new SQLAuthDAO();
    }
    @AfterEach
    public void clearDB() throws DataAccessException{
        authDAO.clearAuthData();
    }

    @Test
    public void clearAuthDataTest() throws DataAccessException{
        authDAO.clearAuthData();
    }

    @Test
    public void createAuthDataSuccess() throws DataAccessException{
        String authToken = authDAO.createAuthData("robert");
        assert authToken != null;
    }

    @Test
    public void getAuthTokenSuccess() throws DataAccessException{
        String authToken1 = authDAO.createAuthData("Karen");
        String authToken2 = authDAO.getAuthToken("Karen");
        assert authToken1.equals(authToken2);
    }
}
