package server;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.*;
import requests.*;
import responses.*;
import server.websocket.WebSocketHandler;
import service.ClearService;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Spark;

public class Server {
    private final GameService gameService;
    private final UserService userService;
    private final ClearService clearService;
    private final WebSocketHandler webSocketHandler;

    public Server() {
        try{
            AuthDAO authDAO = new SQLAuthDAO();
            UserDAO userDAO = new SQLUserDAO();
            GameDAO gameDAO = new SQLGameDAO();
            gameService = new GameService(gameDAO, authDAO);
            userService = new UserService(authDAO, userDAO);
            clearService = new ClearService(authDAO, userDAO, gameDAO);
            this.webSocketHandler = new WebSocketHandler();
        } catch(DataAccessException e){
            throw new RuntimeException(e);
        }


    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");
        Spark.webSocket("/ws", webSocketHandler);
        Spark.post("/user", this::register);
        Spark.delete("/db", this::clearApplication);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.exception(DataAccessException.class, this::exceptionHandler);
        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private void exceptionHandler(DataAccessException ex, Request req, Response res) {
        String exceptionMessage = ex.getMessage();
        switch (exceptionMessage) {
            case "unauthorized" -> res.status(401);
            case "bad request" -> res.status(400);
            case "already taken" -> res.status(403);
            default -> res.status(500);
        }
        String exceptionJson = "{\n \"message\": \"Error: %s\"\n}".formatted(exceptionMessage);
        res.body(exceptionJson);
    }

    private Object joinGame(Request req, Response res) throws DataAccessException {
        String authToken = req.headers("Authorization");
        JoinGameRequest joinGameBodyInfo = new Gson().fromJson(req.body(), JoinGameRequest.class);
        ChessGame.TeamColor playerColor = joinGameBodyInfo.playerColor();
        int gameID = joinGameBodyInfo.gameID();
        JoinGameRequest joinGameRequest = new JoinGameRequest(authToken, playerColor, gameID);
        JoinGameResponse joinGameResponse = gameService.joinGame(joinGameRequest);
        res.status(200);
        return new Gson().toJson(joinGameResponse);
    }

    private Object createGame(Request req, Response res) throws DataAccessException {
        String authToken = req.headers("Authorization");
        CreateGameRequest createGameName = new Gson().fromJson(req.body(), CreateGameRequest.class);
        String name = createGameName.gameName();
        CreateGameRequest createGameRequest = new CreateGameRequest(authToken, name);
        CreateGameResponse createGameResponse = gameService.createGame(createGameRequest);
        res.status(200);
        return new Gson().toJson(createGameResponse);
    }

    private Object listGames(Request req, Response res) throws DataAccessException {
        ListGamesRequest listGamesRequest = new ListGamesRequest(req.headers("Authorization"));
        ListGamesResponse listGamesResponse = gameService.listGames(listGamesRequest);
        res.status(200);
        return new Gson().toJson(listGamesResponse);
    }

    private Object logout(Request req, Response res) throws DataAccessException {
        LogoutRequest logoutRequest = new LogoutRequest(req.headers("Authorization"));
        LogoutResponse logoutResponse = userService.logout(logoutRequest);
        res.status(200);
        return new Gson().toJson(logoutResponse);
    }

    private Object login(Request req, Response res) throws DataAccessException {
        LoginRequest loginRequest = new Gson().fromJson(req.body(), LoginRequest.class);
        LoginResponse loginResponse = userService.login(loginRequest);
        res.status(200);
        return new Gson().toJson(loginResponse);
    }

    private Object register(Request req, Response res) throws DataAccessException {
        RegisterRequest registerRequest = new Gson().fromJson(req.body(), RegisterRequest.class);
        RegisterResponse registerResponse = userService.register(registerRequest);
        res.status(200);
        return new Gson().toJson(registerResponse);
    }

    private Object clearApplication(Request req, Response res) throws DataAccessException{
        clearService.clear();
        res.status(200);
        return "";
    }
}
