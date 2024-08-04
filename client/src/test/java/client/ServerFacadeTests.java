package client;

import dataaccess.DataAccessException;
import org.junit.jupiter.api.*;
import responses.LoginResponse;
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
        Assertions.assertTrue( registerResponse.authToken()!=null && Objects.equals(registerResponse.username(), "player1"));
    }

    @Test
    public void registerTwiceFail() throws DataAccessException {
        facade.register("player1", "password", "p1@email.com");
        Assertions.assertThrows(DataAccessException.class, () ->facade.register("player1", "subir", "pronto@email.com"));
    }

    @Test
    public void loginSuccess() throws DataAccessException {
        facade.register("nada", "password", "ninguem@email.com");
        LoginResponse loginResponse = facade.login("nada", "password");
        Assertions.assertTrue(loginResponse.authToken()!=null && Objects.equals(loginResponse.username(), "nada"));
    }

    @Test
    public void loginBadPassword() throws DataAccessException {
        facade.register("nada", "password", "ninguem@email.com");
        Assertions.assertThrows(DataAccessException.class, () -> facade.login("nada", "mal"));
    }

    @Test
    public void clear() throws DataAccessException {
        facade.register("nada", "password", "ninguem@email.com");
        facade.register("player1", "password", "p1@email.com");
        facade.register("bob", "carlisle", "bobc@email.com");
        facade.clearApplication();
        Assertions.assertThrows(DataAccessException.class, () -> facade.login("nada", "password"));
        Assertions.assertThrows(DataAccessException.class, () -> facade.login("player1", "password"));
        Assertions.assertThrows(DataAccessException.class, () -> facade.login("bob", "carlisle"));
    }

    @Test
    public void logoutSuccess() throws DataAccessException {
        RegisterResponse registerResponse = facade.register("nada", "password", "ninguem@email.com");
        facade.logout(registerResponse.authToken());
    }

}
