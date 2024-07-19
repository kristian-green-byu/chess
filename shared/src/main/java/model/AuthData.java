package model;

public class AuthData {
    private String authToken;
    private String username;
    AuthData(String authToken, String username) {
        this.authToken = authToken;
        this.username = username;
    }
}
