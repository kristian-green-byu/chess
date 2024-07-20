package server;

import service.AuthService;
import service.GameService;
import service.UserService;
import spark.*;

public class Server {
    private final AuthService authService;
    private final GameService gameService;
    private final UserService userService;
    public Server() {
        authService = new AuthService();
        gameService = new GameService();
        userService = new UserService();
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
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

    //handlers
    private Object clearAll(Request req, Response res) {
        authService.clearApplication();
        gameService.clearApplication();
        userService.clearApplication();
        res.status(200);
        return "";
    }
}
