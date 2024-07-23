package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;

public interface GameDAO {
    int createGame(String name);

    GameData getGame(int gameID);

    void updateGame(String name, ChessGame.TeamColor playerColor, GameData gameData);

    Collection<GameData> listGames();

    void clearGameData();
}
