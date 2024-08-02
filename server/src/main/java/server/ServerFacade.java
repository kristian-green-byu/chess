package server;

import chess.ChessGame;
import dataaccess.DataAccessException;
import requests.*;

import java.net.*;
import com.google.gson.Gson;
import java.io.*;


public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(String url) {
        this.serverUrl = url;
    }

    public Object register(String username, String password, String email) throws DataAccessException {
        var path = "/user";
        RegisterRequest register = new RegisterRequest(username, password, email);
        return this.makeRequest("POST", path, register, RegisterRequest.class);
    }

    public Object clearApplication() throws DataAccessException {
        var path = "/db";
        return this.makeRequest("DELETE", path, null, null);
    }

    public Object login(String username, String password) throws DataAccessException {
        var path = "/session";
        LoginRequest login = new LoginRequest(username, password);
        return this.makeRequest("POST", path, login, LoginRequest.class);
    }

    public Object logout(String authToken) throws DataAccessException {
        var path = "/session";
        LogoutRequest logout = new LogoutRequest(authToken);
        return this.makeRequest("DELETE", path, logout, LogoutRequest.class);
    }

    public Object listGames(String authToken) throws DataAccessException {
        var path = "/game";
        ListGamesRequest listGames = new ListGamesRequest(authToken);
        return this.makeRequest("GET", path, listGames, ListGamesRequest.class);
    }

    public Object createGame(String authToken, String gameName) throws DataAccessException {
        var path = "/game";
        CreateGameRequest createGame = new CreateGameRequest(authToken, gameName);
        return this.makeRequest("POST", path, createGame, CreateGameRequest.class);
    }

    public Object joinGame(String authToken, ChessGame.TeamColor playerColor, int gameID) throws DataAccessException {
        var path = "/game";
        JoinGameRequest joinGame = new JoinGameRequest(authToken, playerColor, gameID);
        return this.makeRequest("PUT", path, joinGame, JoinGameRequest.class);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws DataAccessException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
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
