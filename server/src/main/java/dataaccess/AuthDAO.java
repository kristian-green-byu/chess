package dataaccess;

import model.AuthData;

public interface AuthDAO {
    String createAuthData(String username);

    String getAuthToken(String username);

    AuthData getAuthData(String authToken);

    void clearAuthData();

    void deleteAuthData(AuthData authData);
}
