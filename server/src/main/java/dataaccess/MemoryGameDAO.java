package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.HashMap;
import java.util.UUID;

public class MemoryGameDAO implements GameDAO{
    final private HashMap<Integer, GameData> games = new HashMap<>();
    private int nextID = 1;

    public int createGame(String name){
        int gameID = UUID.randomUUID().hashCode();
        ChessGame game = new ChessGame();
        GameData gameData = new GameData(gameID, null, null, name, game);
        games.put(nextID++, gameData);
        return gameID;
    }

    public GameData getGame(int gameID){
        for(GameData gameData : games.values()){
            if(gameData.gameID()==gameID){
                return gameData;
            }
        }
        return null;
    }
}
