package client;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.*;
import responses.CreateGameResponse;
import responses.ListGamesResponse;
import responses.LoginResponse;
import responses.RegisterResponse;
import server.Server;
import serverfacade.ServerFacade;

import java.io.IOException;
import java.util.Collection;
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

    @AfterEach
    void clearSQL() throws IOException {
        facade.clearApplication();
    }

    @AfterAll
    static void stopServer(){
        server.stop();
    }


    @Test
    public void registerSuccess() throws IOException {
        RegisterResponse registerResponse = facade.register("player1", "password", "p1@email.com");
        Assertions.assertTrue( registerResponse.authToken()!=null && Objects.equals(registerResponse.username(), "player1"));
    }

    @Test
    public void registerTwiceFail() throws IOException {
        facade.register("player1", "password", "p1@email.com");
        Assertions.assertThrows(IOException.class, () ->facade.register("player1", "subir", "pronto@email.com"));
    }

    @Test
    public void loginSuccess() throws IOException {
        facade.register("nada", "password", "ninguem@email.com");
        LoginResponse loginResponse = facade.login("nada", "password");
        Assertions.assertTrue(loginResponse.authToken()!=null && Objects.equals(loginResponse.username(), "nada"));
    }

    @Test
    public void loginBadPassword() throws IOException {
        facade.register("nada", "password", "ninguem@email.com");
        Assertions.assertThrows(IOException.class, () -> facade.login("nada", "mal"));
    }

    @Test
    public void clearWithData() throws IOException {
        facade.register("nada", "password", "ninguem@email.com");
        facade.register("player1", "password", "p1@email.com");
        facade.register("bob", "carlisle", "bobc@email.com");
        facade.clearApplication();
        Assertions.assertThrows(IOException.class, () -> facade.login("nada", "password"));
        Assertions.assertThrows(IOException.class, () -> facade.login("player1", "password"));
        Assertions.assertThrows(IOException.class, () -> facade.login("bob", "carlisle"));
    }

    @Test
    public void clearWithoutData() {
        Assertions.assertDoesNotThrow(() ->facade.clearApplication());
    }

    @Test
    public void logoutSuccess() throws IOException {
        RegisterResponse registerResponse = facade.register("nada", "password", "ninguem@email.com");
        facade.logout(registerResponse.authToken());
    }

    @Test
    public void logoutNotLoggedIn() {
        Assertions.assertThrows(IOException.class, () -> facade.logout("nada"));
    }

    @Test
    public void listGamesSuccess() throws IOException  {
        RegisterResponse registerResponse = facade.register("robert", "mcdonald", "mcds@email.com");
        facade.createGame(registerResponse.authToken(), "test");
        facade.createGame(registerResponse.authToken(), "test2");
        ListGamesResponse gameResponse = facade.listGames(registerResponse.authToken());
        Collection<GameData> games = gameResponse.games();
        Assertions.assertEquals(2, games.size());
    }

    @Test
    public void listGamesNoAuth() throws IOException {
        RegisterResponse registerResponse = facade.register("Kelly", "Hansen", "khs@email.com");
        facade.createGame(registerResponse.authToken(), "test");
        facade.createGame(registerResponse.authToken(), "test2");
        facade.logout(registerResponse.authToken());
        Assertions.assertThrows(IOException.class, () -> facade.listGames(registerResponse.authToken()));
    }

    @Test
    public void joinGameSuccess() throws IOException {
        RegisterResponse registerResponse = facade.register("robert", "mcdonald", "mcds@email.com");
        CreateGameResponse response = facade.createGame(registerResponse.authToken(), "test");
        facade.joinGame(registerResponse.authToken(), ChessGame.TeamColor.WHITE, response.gameID());
        ListGamesResponse listGamesResponse = facade.listGames(registerResponse.authToken());
        boolean bobFound = false;
        for (GameData gameData : listGamesResponse.games()) {
            if (Objects.equals(gameData.whiteUsername(), "robert")) {
                bobFound = true;
                break;
            }
        }
        Assertions.assertTrue(bobFound);
    }

    @Test
    public void joinGameNotLoggedIn() throws IOException {
        RegisterResponse registerResponse = facade.register("robert", "mcdonald", "mcds@email.com");
        CreateGameResponse response = facade.createGame(registerResponse.authToken(), "test");
        facade.logout(registerResponse.authToken());
        Assertions.assertThrows(IOException.class
                , () -> facade.joinGame(registerResponse.authToken(), ChessGame.TeamColor.WHITE, response.gameID()));
    }

    @Test
    public void createGameSuccess() throws IOException {
        RegisterResponse registerResponse = facade.register("robert", "mcdonald", "mcds@email.com");
        facade.createGame(registerResponse.authToken(), "test");
        ListGamesResponse listGamesResponse = facade.listGames(registerResponse.authToken());
        Assertions.assertEquals(1, listGamesResponse.games().size());
    }

    @Test
    public void createGameNoAuth(){
        Assertions.assertThrows(IOException.class, ()-> facade.createGame("fake", "test"));
    }

}
