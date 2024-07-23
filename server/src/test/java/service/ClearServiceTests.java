package service;

import chess.ChessGame;
import dataaccess.*;
import org.junit.jupiter.api.Test;
import requests.ClearRequest;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import requests.RegisterRequest;
import responses.CreateGameResponse;
import responses.RegisterResponse;


public class ClearServiceTests {
    AuthDAO authDAO = new MemoryAuthDAO();
    GameDAO gameDAO = new MemoryGameDAO();
    UserDAO userDAO = new MemoryUserDAO();
    UserService userService = new UserService(authDAO, userDAO);
    GameService gameService = new GameService(gameDAO, authDAO);
    ClearService clearService = new ClearService(authDAO, userDAO, gameDAO);

    @Test
    public void clear() throws DataAccessException{
        RegisterRequest registerRequest = new RegisterRequest("testuser", "testpass", "test@email.com");
        RegisterResponse registerResponse = userService.register(registerRequest);
        String authToken = registerResponse.authToken();
        CreateGameRequest createGameRequest = new CreateGameRequest(authToken, "testgame");
        CreateGameResponse createGameResponse = gameService.createGame(createGameRequest);
        JoinGameRequest joinGameRequest = new JoinGameRequest(authToken, ChessGame.TeamColor.BLACK, createGameResponse.gameID());
        gameService.joinGame(joinGameRequest);
        clearService.clear(new ClearRequest());
        assert(authDAO.getAuthData(authToken)==null && userDAO.getUser("testuser")==null && gameDAO.getGame(createGameResponse.gameID())==null);
    }
}
