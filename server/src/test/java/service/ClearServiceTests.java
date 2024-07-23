package service;

import chess.ChessGame;
import dataaccess.*;
import org.junit.jupiter.api.Test;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import requests.RegisterRequest;
import responses.CreateGameResponse;
import responses.RegisterResponse;


public class ClearServiceTests {
    final AuthDAO authDAO = new MemoryAuthDAO();
    final GameDAO gameDAO = new MemoryGameDAO();
    final UserDAO userDAO = new MemoryUserDAO();
    final UserService userService = new UserService(authDAO, userDAO);
    final GameService gameService = new GameService(gameDAO, authDAO);
    final ClearService clearService = new ClearService(authDAO, userDAO, gameDAO);

    @Test
    public void clear() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("testUser", "testPass", "test@email.com");
        RegisterResponse registerResponse = userService.register(registerRequest);
        String authToken = registerResponse.authToken();
        CreateGameRequest createGameRequest = new CreateGameRequest(authToken, "testGame");
        CreateGameResponse createGameResponse = gameService.createGame(createGameRequest);
        JoinGameRequest joinGameRequest = new JoinGameRequest(authToken, ChessGame.TeamColor.BLACK, createGameResponse.gameID());
        gameService.joinGame(joinGameRequest);
        clearService.clear();
        assert (authDAO.getAuthData(authToken) == null && userDAO.getUser("testUser") == null && gameDAO.getGame(createGameResponse.gameID()) == null);
    }
}
