package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Objects;

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
        assert game != null && Objects.equals(game.gameName(), "Test Game");
    }

    @Test
    public void createGameNullGameName(){
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.createGame(null));
    }

    @Test
    public void getGameSuccess() throws DataAccessException{
        int gameID = gameDAO.createGame("myGame");
        GameData game = gameDAO.getGame(gameID);
        assert game != null;
    }

    @Test
    public void getGameInvalidID()throws DataAccessException{
        gameDAO.createGame("myGame");
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.getGame(-50));
    }

    @Test
    public void updateGameSuccess() throws DataAccessException{
        int gameID = gameDAO.createGame("Fun Game");
        GameData game = gameDAO.getGame(gameID);
        gameDAO.updateGame("Bob", ChessGame.TeamColor.WHITE, game);
        GameData gameUpdated = gameDAO.getGame(gameID);
        assert Objects.equals(gameUpdated.whiteUsername(), "Bob");
    }

    @Test
    public void updateGameInvalidName() throws DataAccessException{
        int gameID = gameDAO.createGame("Chess1");
        GameData game = gameDAO.getGame(gameID);
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.updateGame(null, ChessGame.TeamColor.WHITE, game));
    }

    @Test
    public void listGamesSuccess() throws DataAccessException{
        gameDAO.createGame("coolGame");
        gameDAO.createGame("coolerGame");
        gameDAO.createGame("coolestGame");
        Collection<GameData> games = gameDAO.listGames();
        for(GameData game: games){
            assert game != null;
        }
        assert games.size()==3;
    }

    @Test
    public void listGamesAfterClear() throws DataAccessException{
        gameDAO.createGame("aGame");
        gameDAO.createGame("anotherGame");
        gameDAO.createGame("gamerGame");
        gameDAO.createGame("chessGame");
        gameDAO.clearGameData();
        Collection<GameData> games = gameDAO.listGames();
        assert games.isEmpty();
    }
}
