package service;

import dataaccess.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import requests.CreateGameRequest;
import requests.RegisterRequest;
import responses.CreateGameResponse;
import responses.RegisterResponse;

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
}
