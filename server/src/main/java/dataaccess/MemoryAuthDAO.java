package dataaccess;

import model.AuthData;

import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {
    final private HashSet<AuthData> authDataSet = new HashSet<>();

    public String createAuthData(String username) {
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, username);
        authDataSet.add(authData);
        return authToken;
    }

    public String getAuthToken(String username) {
        for (AuthData authData : authDataSet) {
            if (Objects.equals(authData.username(), username)) {
                return authData.authToken();
            }
        }
        return null;
    }

    public void clearAuthData() {
        authDataSet.clear();
    }

    public AuthData getAuthData(String authToken) {
        for (AuthData authData : authDataSet) {
            if (authData.authToken().equals(authToken)) {
                return authData;
            }
        }
        return null;
    }

    public void deleteAuthData(AuthData authData) {
        authDataSet.remove(authData);
    }
}
