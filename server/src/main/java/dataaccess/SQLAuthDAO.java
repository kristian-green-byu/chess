package dataaccess;

import model.AuthData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class SQLAuthDAO implements AuthDAO{

    private final DatabaseManager databaseManager;

    public SQLAuthDAO() throws DataAccessException{
        this.databaseManager = new DatabaseManager();
    }

    public String createAuthData(String username) throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        var statement = databaseManager.setDB("INSERT INTO %DB_NAME%.authData (authToken, username) VALUES (?, ?)");
        databaseManager.executeUpdate(statement, authToken, username);
        return authToken;
    }

    public String getAuthToken(String username) throws DataAccessException{
        if(username==null){
            throw new DataAccessException("unauthorized");
        }
        Collection<AuthData> auths = getAuthDataCollection();
        for (AuthData auth : auths) {
            if (auth.username().equals(username)) {
                return auth.authToken();
            }
        }
        return null;
    }

    private AuthData readAuthData(ResultSet rs) throws SQLException {
        var username = rs.getString("username");
        var authToken = rs.getString("authToken");

        return new AuthData(authToken, username);
    }

    public AuthData getAuthData(String authToken) throws DataAccessException {
        if(authToken==null){
            throw new DataAccessException("unauthorized");
        }
        Collection<AuthData> auths = getAuthDataCollection();
        for (AuthData auth : auths) {
            if (auth.authToken().equals(authToken)) {
                return auth;
            }
        }
        return null;
    }

    private Collection<AuthData> getAuthDataCollection() throws DataAccessException {
        Collection<AuthData> auths = new ArrayList<>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = databaseManager.setDB("SELECT authToken, username FROM %DB_NAME%.authData");
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        auths.add(readAuthData(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return auths;
    }

    public void clearAuthData() throws DataAccessException{
        var statement = databaseManager.setDB("TRUNCATE %DB_NAME%.authData");
        databaseManager.executeUpdate(statement);
    }

    public void deleteAuthData(AuthData authData) throws DataAccessException {
        if (authData == null) {
            throw new DataAccessException("unauthorized");
        }
        Collection<AuthData> auths = getAuthDataCollection();
        for (AuthData auth : auths) {
            if (auth.equals(authData)) {
                var statement = databaseManager.setDB("DELETE FROM %DB_NAME%.authData WHERE authToken = ?");
                databaseManager.executeUpdate(statement, auth.authToken());
            }
        }
    }
}
