package service;

import chess.ChessGame;
import dataaccess.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import requests.ListGamesRequest;
import requests.RegisterRequest;
import responses.CreateGameResponse;
import responses.JoinGameResponse;
import responses.ListGamesResponse;
import responses.RegisterResponse;

import java.util.Objects;

public class GameServiceTests {
    final AuthDAO authDAO = new MemoryAuthDAO();
    final GameDAO gameDAO = new MemoryGameDAO();
    final UserDAO userDAO = new MemoryUserDAO();
    final UserService userService = new UserService(authDAO, userDAO);
    final GameService gameService = new GameService(gameDAO, authDAO);

    @Test
    public void createGameSuccess() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("testUser", "testPass", "test@email.com");
        RegisterResponse registerResponse = userService.register(registerRequest);
        String authToken = registerResponse.authToken();
        CreateGameRequest createGameRequest = new CreateGameRequest(authToken, "testGame");
        CreateGameResponse createGameResponse = gameService.createGame(createGameRequest);
        assert createGameResponse != null && gameDAO.getGame(createGameResponse.gameID()) != null;
    }

    @Test
    public void createGameWithoutAuth() {
        CreateGameRequest createGameRequest = new CreateGameRequest("fakeToken", "testGame");
        Assertions.assertThrows(DataAccessException.class, () -> gameService.createGame(createGameRequest));
    }

    @Test
    public void joinGameSuccess() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("testUser", "testPass", "test@email.com");
        RegisterResponse registerResponse = userService.register(registerRequest);
        String authToken = registerResponse.authToken();
        CreateGameRequest createGameRequest = new CreateGameRequest(authToken, "testGame");
        CreateGameResponse createGameResponse = gameService.createGame(createGameRequest);
        JoinGameRequest joinGameRequest = new JoinGameRequest(authToken, ChessGame.TeamColor.BLACK, createGameResponse.gameID());
        JoinGameResponse joinGameResponse = gameService.joinGame(joinGameRequest);
        assert joinGameResponse != null && Objects.equals(gameDAO.getGame(joinGameRequest.gameID()).blackUsername(), "testUser");
    }

    @Test
    public void joinTakenColor() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("testUser", "testPass", "test@email.com");
        RegisterResponse registerResponse = userService.register(registerRequest);
        String authToken = registerResponse.authToken();
        CreateGameRequest createGameRequest = new CreateGameRequest(authToken, "testGame");
        CreateGameResponse createGameResponse = gameService.createGame(createGameRequest);
        JoinGameRequest joinGameRequest = new JoinGameRequest(authToken, ChessGame.TeamColor.BLACK, createGameResponse.gameID());
        gameService.joinGame(joinGameRequest);
        RegisterRequest registerRequest2 = new RegisterRequest("testUser2", "testPass2", "test@email.com2");
        RegisterResponse registerResponse2 = userService.register(registerRequest2);
        String authToken2 = registerResponse2.authToken();
        JoinGameRequest joinGameRequest2 = new JoinGameRequest(authToken2, ChessGame.TeamColor.BLACK, createGameResponse.gameID());
        Assertions.assertThrows(DataAccessException.class, () -> gameService.joinGame(joinGameRequest2));
    }

    @Test
    public void listGamesSuccess() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("testUser", "testPass", "test@email.com");
        RegisterResponse registerResponse = userService.register(registerRequest);
        String authToken = registerResponse.authToken();
        CreateGameRequest createGameRequest = new CreateGameRequest(authToken, "testGame");
        CreateGameResponse createGameResponse = gameService.createGame(createGameRequest);
        ListGamesResponse listGamesResponse = gameService.listGames(new ListGamesRequest(authToken));
        assert listGamesResponse.games().contains(gameDAO.getGame(createGameResponse.gameID()));
    }

    @Test
    public void listGamesUnauthorized() {
        Assertions.assertThrows(DataAccessException.class, () -> gameService.listGames(new ListGamesRequest("hax")));
    }
}
