package client;

import dataaccess.DataAccessException;
import org.junit.jupiter.api.*;
import responses.RegisterResponse;
import server.Server;
import server.ServerFacade;

import java.util.Objects;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:"+port);
    }

    @AfterAll
    static void stopServer() throws DataAccessException {
        facade.clearApplication();
        server.stop();
    }


    @Test
    public void registerSuccess() throws DataAccessException {
        RegisterResponse registerResponse = facade.register("player1", "password", "p1@email.com");
        assert registerResponse.authToken()!=null && Objects.equals(registerResponse.username(), "player1");
    }

}
