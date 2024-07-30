package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;

public interface GameDAO {
    int createGame(String name) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    void updateGame(String name, ChessGame.TeamColor playerColor, GameData gameData) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

    void clearGameData() throws DataAccessException;
}
