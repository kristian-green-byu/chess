package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class MemoryGameDAO implements GameDAO {
    final private HashMap<Integer, GameData> games = new HashMap<>();
    private int nextID = 1;

    public int createGame(String name) {
        int gameID = UUID.randomUUID().hashCode();
        if (gameID < 0){
            gameID = -gameID;
        }
        ChessGame game = new ChessGame();
        GameData gameData = new GameData(gameID, null, null, name, game);
        games.put(nextID++, gameData);
        return gameID;
    }

    public GameData getGame(int gameID) {
        for (GameData gameData : games.values()) {
            if (gameData.gameID() == gameID) {
                return gameData;
            }
        }
        return null;
    }

    public void updateGame(String newName, ChessGame.TeamColor playerColor, GameData gameData) {
        int id = 1;
        for (GameData game : games.values()) {
            if (gameData == game) {
                break;
            }
            id++;
        }
        GameData game = games.get(id);
        GameData newGameData;
        if (playerColor == ChessGame.TeamColor.BLACK) {
            newGameData = new GameData(gameData.gameID(), game.whiteUsername(), newName, game.gameName(), game.game());
        } else {
            newGameData = new GameData(gameData.gameID(), newName, game.blackUsername(), game.gameName(), game.game());
        }
        games.remove(id);
        games.put(id, newGameData);
    }

    public Collection<GameData> listGames() {
        return games.values();
    }

    public void clearGameData() {
        games.clear();
        nextID = 1;
    }
}
