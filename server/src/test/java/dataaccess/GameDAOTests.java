package dataaccess;

import model.GameData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class GameDAOTests {
    private static GameDAO gameDAO;

    public GameDAOTests() throws DataAccessException{
        gameDAO = new SQLGameDAO();
    }

    @AfterEach
    public void clearDB() throws DataAccessException{
        gameDAO.clearGameData();
    }

    @Test
    public void clearGameDataTest() throws DataAccessException{
        int gameID1 = gameDAO.createGame("Test Game1");
        int gameID2 = gameDAO.createGame("Test Game2");
        gameDAO.clearGameData();
        assert gameDAO.getGame(gameID1) == null && gameDAO.getGame(gameID2) == null;
    }

    @Test
    public void createGameSuccess() throws DataAccessException{
        int gameID = gameDAO.createGame("Test Game");
        GameData game = gameDAO.getGame(gameID);
        assert game != null;
    }

    @Test
    public void getGameSuccess() throws DataAccessException{
        int gameID = gameDAO.createGame("myGame");
        GameData game = gameDAO.getGame(gameID);
        assert game != null;
    }
}
