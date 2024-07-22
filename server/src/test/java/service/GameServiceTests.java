package service;

import chess.ChessGame;
import dataaccess.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import requests.RegisterRequest;
import responses.CreateGameResponse;
import responses.JoinGameResponse;
import responses.RegisterResponse;

import java.util.Objects;

public class GameServiceTests {
    AuthDAO authDAO = new MemoryAuthDAO();
    GameDAO gameDAO = new MemoryGameDAO();
    UserDAO userDAO = new MemoryUserDAO();
    UserService userService = new UserService(authDAO, userDAO);
    GameService gameService = new GameService(gameDAO, authDAO);

    @Test
    public void createGameSuccess() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("testuser", "testpass", "test@email.com");
        RegisterResponse registerResponse = userService.register(registerRequest);
        String authToken = registerResponse.authToken();
        CreateGameRequest createGameRequest = new CreateGameRequest(authToken, "testgame");
        CreateGameResponse createGameResponse = gameService.createGame(createGameRequest);
        assert createGameResponse != null && gameDAO.getGame(createGameResponse.gameID())!=null;
    }

    @Test
    public void createGameWithoutAuth() {
        CreateGameRequest createGameRequest = new CreateGameRequest("faketoken", "testgame");
        Assertions.assertThrows(DataAccessException.class, () -> gameService.createGame(createGameRequest));
    }

    @Test
    public void joinGameSuccess() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("testuser", "testpass", "test@email.com");
        RegisterResponse registerResponse = userService.register(registerRequest);
        String authToken = registerResponse.authToken();
        CreateGameRequest createGameRequest = new CreateGameRequest(authToken, "testgame");
        CreateGameResponse createGameResponse = gameService.createGame(createGameRequest);
        JoinGameRequest joinGameRequest = new JoinGameRequest(authToken, ChessGame.TeamColor.BLACK, createGameResponse.gameID());
        JoinGameResponse joinGameResponse = gameService.joinGame(joinGameRequest);
        assert joinGameResponse != null && Objects.equals(gameDAO.getGame(joinGameRequest.gameID()).blackUsername(), "testuser");
    }

    @Test
    public void joinTakenColor() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("testuser", "testpass", "test@email.com");
        RegisterResponse registerResponse = userService.register(registerRequest);
        String authToken = registerResponse.authToken();
        CreateGameRequest createGameRequest = new CreateGameRequest(authToken, "testgame");
        CreateGameResponse createGameResponse = gameService.createGame(createGameRequest);
        JoinGameRequest joinGameRequest = new JoinGameRequest(authToken, ChessGame.TeamColor.BLACK, createGameResponse.gameID());
        gameService.joinGame(joinGameRequest);
        RegisterRequest registerRequest2 = new RegisterRequest("testuser2", "testpass2", "test@email.com2");
        RegisterResponse registerResponse2 = userService.register(registerRequest2);
        String authToken2 = registerResponse2.authToken();
        JoinGameRequest joinGameRequest2 = new JoinGameRequest(authToken2, ChessGame.TeamColor.BLACK, createGameResponse.gameID());
        Assertions.assertThrows(DataAccessException.class, () -> gameService.joinGame(joinGameRequest2));
    }
}
