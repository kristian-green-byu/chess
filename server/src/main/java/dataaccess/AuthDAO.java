package dataaccess;

import model.AuthData;

public interface AuthDAO {
    String createAuthData(String username);
    String getAuthToken(String username);
}
