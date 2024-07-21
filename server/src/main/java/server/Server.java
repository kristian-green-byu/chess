package server;

import com.google.gson.Gson;
import dataaccess.*;
import requests.RegisterRequest;
import responses.RegisterResponse;
import service.AuthService;
import service.GameService;
import service.UserService;
import spark.*;
import model.AuthData;

public class Server {
    private final AuthService authService;
    private final GameService gameService;
    private final UserService userService;
    public Server() {
        AuthDAO authDAO = new MemoryAuthDAO();
        authService = new AuthService();
        gameService = new GameService();
        userService = new UserService(authDAO);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::register);
        Spark.delete("/db", this::clearAll);
        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object register(Request req, Response res) throws DataAccessException{
        RegisterRequest registerRequest = new Gson().fromJson(req.body(), RegisterRequest.class);
        RegisterResponse registerResponse = userService.register(registerRequest);
        return new Gson().toJson(registerResponse);
    }
    private Object clearAll(Request req, Response res) throws DataAccessException {
        authService.clearAuth();
        gameService.clearGames();
        userService.clearUsers();
        res.status(200);
        return "";
    }
}
