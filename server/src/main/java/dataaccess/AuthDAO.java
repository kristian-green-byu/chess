package dataaccess;

import model.AuthData;

public interface AuthDAO {
    String createAuthData(String username) throws DataAccessException;

    String getAuthToken(String username) throws DataAccessException;

    AuthData getAuthData(String authToken);

    void clearAuthData() throws DataAccessException;

    void deleteAuthData(AuthData authData);
}
