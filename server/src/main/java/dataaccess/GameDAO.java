package dataaccess;
import model.GameData;
import chess.ChessGame;

import java.util.Collection;

public interface GameDAO {
    int createGame(String name);
    GameData getGame(int gameID);
    void updateGame(String name, ChessGame.TeamColor playercolor, GameData gameData);
    Collection<GameData> listGames();
}
