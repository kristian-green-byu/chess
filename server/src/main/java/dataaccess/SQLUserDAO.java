package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class SQLUserDAO implements UserDAO{

    private final DatabaseManager databaseManager;

    public SQLUserDAO() throws DataAccessException{
        this.databaseManager = new DatabaseManager();
    }

    public UserData getUser(String username) throws DataAccessException {
        if(username==null){
            throw new DataAccessException("unauthorized");
        }
        Collection<UserData> users = new ArrayList<>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = databaseManager.setDB("SELECT username, password, email FROM %DB_NAME%.userData");
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        users.add(readUserData(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        for (UserData user : users) {
            if (user.username().equals(username)) {
                return user;
            }
        }
        return null;
    }

    private UserData readUserData(ResultSet rs) throws SQLException {
        var username = rs.getString("username");
        var password = rs.getString("password");
        var email = rs.getString("email");

        return new UserData(username, password, email);
    }

    public void createUser(UserData user) throws DataAccessException{
        if(getUser(user.username())!=null){
            throw new DataAccessException("already taken");
        }
        var statement = databaseManager.setDB("INSERT INTO %DB_NAME%.userData (username, password, email) VALUES (?, ?, ?)");
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        databaseManager.executeUpdate(statement, user.username(), hashedPassword, user.email());
    }

    public void clearUserData() throws DataAccessException {
        var statement = databaseManager.setDB("TRUNCATE %DB_NAME%.userData");
        databaseManager.executeUpdate(statement);
    }
}
