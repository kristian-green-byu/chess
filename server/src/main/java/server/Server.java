package server;

import com.google.gson.Gson;
import dataaccess.*;
import requests.*;
import responses.*;
import service.GameService;
import service.UserService;
import spark.*;

public class Server {
    private final GameService gameService;
    private final UserService userService;
    public Server() {
        AuthDAO authDAO = new MemoryAuthDAO();
        UserDAO userDAO = new MemoryUserDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        gameService = new GameService(gameDAO, authDAO);
        userService = new UserService(authDAO, userDAO);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::register);
        Spark.delete("/db", this::clearAll);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object joinGame(Request req, Response res) throws DataAccessException {
        JoinGameRequest joinGameRequest = new Gson().fromJson(req.body(), JoinGameRequest.class);
        JoinGameResponse joinGameResponse = gameService.joinGame(joinGameRequest);
        return new Gson().toJson(joinGameResponse);
    }

    private Object createGame(Request req, Response res) throws DataAccessException{
        CreateGameRequest createGameRequest = new Gson().fromJson(req.body(), CreateGameRequest.class);
        CreateGameResponse createGameResponse = gameService.createGame(createGameRequest);
        return new Gson().toJson(createGameResponse);
    }

    private Object listGames(Request req, Response res) throws DataAccessException {
        ListGamesRequest listGamesRequest = new Gson().fromJson(req.headers("Authorization"), ListGamesRequest.class);
        ListGamesResponse listGamesResponse = gameService.listGames(listGamesRequest);
        return new Gson().toJson(listGamesResponse);
    }

    private Object logout(Request req, Response res) throws DataAccessException{
        LogoutRequest logoutRequest = new Gson().fromJson(req.headers("Authorization"), LogoutRequest.class);
        LogoutResponse LogoutResponse = userService.logout(logoutRequest);
        return new Gson().toJson(LogoutResponse);
    }

    private Object login(Request req, Response res) throws DataAccessException {
        LoginRequest loginRequest = new Gson().fromJson(req.body(), LoginRequest.class);
        LoginResponse loginResponse = userService.login(loginRequest);
        res.status(200);
        return new Gson().toJson(loginResponse);
    }

    private Object register(Request req, Response res) throws DataAccessException{
        RegisterRequest registerRequest = new Gson().fromJson(req.body(), RegisterRequest.class);
        RegisterResponse registerResponse = userService.register(registerRequest);
        res.status(200);
        return new Gson().toJson(registerResponse);
    }

    private Object clearAll(Request req, Response res){
        gameService.clearGames();
        userService.clearUsers();
        res.status(200);
        return "";
    }
}
