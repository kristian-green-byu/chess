package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

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
        String authToken1 = authDAO.createAuthData("Tester");
        String authToken2 = authDAO.createAuthData("user");
        authDAO.clearAuthData();
        AuthData authData1 = authDAO.getAuthData(authToken1);
        AuthData authData2 = authDAO.getAuthData(authToken2);
        assert authData1 == null && authData2 == null;
    }

    @Test
    public void createAuthDataSuccess() throws DataAccessException{
        String authToken = authDAO.createAuthData("Robert");
        assert authToken != null;
    }

    @Test
    public void createAuthDataFail(){
        Assertions.assertThrows(DataAccessException.class, () ->authDAO.createAuthData(null));
    }

    @Test
    public void getAuthTokenSuccess() throws DataAccessException{
        String authToken1 = authDAO.createAuthData("Karen");
        String authToken2 = authDAO.getAuthToken("Karen");
        assert authToken1.equals(authToken2);
    }

    @Test
    public void getAuthTokenFail(){
        Assertions.assertThrows(DataAccessException.class, () ->authDAO.getAuthToken(null));
    }

    @Test
    public void getAuthDataSuccess() throws DataAccessException{
        String authToken = authDAO.createAuthData("Jacob");
        AuthData authData = authDAO.getAuthData(authToken);
        assert authData != null;
    }

    @Test
    public void getAuthDataFail(){
        Assertions.assertThrows(DataAccessException.class, () ->authDAO.getAuthData(null));
    }

    @Test
    public void deleteAuthDataSuccess() throws DataAccessException{
        String authToken = authDAO.createAuthData("Sophia");
        String authToken2 = authDAO.createAuthData("Paul");
        authDAO.deleteAuthData(new AuthData(authToken, "Sophia"));
        AuthData authData = authDAO.getAuthData(authToken);
        AuthData authData2 = authDAO.getAuthData(authToken2);
        assert authData == null && authData2 != null;
    }

    @Test
    public void deleteAuthDataFail() throws DataAccessException{
        authDAO.createAuthData("Sophia");
        authDAO.createAuthData("Paul");
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.deleteAuthData(null));
    }
}
