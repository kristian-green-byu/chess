package server;

import chess.ChessGame;
import dataaccess.DataAccessException;
import requests.*;

import java.net.*;
import com.google.gson.Gson;
import responses.ListGamesResponse;
import responses.LoginResponse;
import responses.RegisterResponse;

import java.io.*;

public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(String url) {
        this.serverUrl = url;
    }

    public RegisterResponse register(String username, String password, String email) throws DataAccessException {
        var path = "/user";
        RegisterRequest register = new RegisterRequest(username, password, email);
        return this.makeRequest("POST", path, register, RegisterResponse.class, null);
    }

    public void clearApplication() throws DataAccessException {
        var path = "/db";
        this.makeRequest("DELETE", path, null, null, null);
    }

    public LoginResponse login(String username, String password) throws DataAccessException {
        var path = "/session";
        LoginRequest login = new LoginRequest(username, password);
        return this.makeRequest("POST", path, login, LoginResponse.class, null);
    }

    public void logout(String authToken) throws DataAccessException {
        var path = "/session";
        LogoutRequest logout = new LogoutRequest(authToken);
        this.makeRequest("DELETE", path, logout, null, authToken);
    }

    public ListGamesResponse listGames(String authToken) throws DataAccessException {
        var path = "/game";
        return this.makeRequest("GET", path, null, ListGamesResponse.class, authToken);
    }

    public Object createGame(String authToken, String gameName) throws DataAccessException {
        var path = "/game";
        CreateGameRequest createGame = new CreateGameRequest(null,gameName);
        return this.makeRequest("POST", path, createGame, CreateGameRequest.class, authToken);
    }

    public void joinGame(String authToken, ChessGame.TeamColor playerColor, int gameID) throws DataAccessException {
        var path = "/game";
        JoinGameRequest joinGame = new JoinGameRequest(null, playerColor, gameID);
        this.makeRequest("PUT", path, joinGame, JoinGameRequest.class, authToken);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String authToken) throws DataAccessException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http, authToken);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http, String authToken) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            if(authToken !=null) {
                http.addRequestProperty("Authorization", authToken);
            }
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
        else{
            if(authToken !=null) {
                http.addRequestProperty("Authorization", authToken);
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, DataAccessException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new DataAccessException("failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status == 200;
    }
}
