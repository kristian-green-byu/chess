package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;

public class SQLGameDAO implements GameDAO{
    public int createGame(String name){
        return 0;
    }

    public GameData getGame(int gameID){
        return null;
    }

    public void updateGame(String name, ChessGame.TeamColor playerColor, GameData gameData){
    }

    public Collection<GameData> listGames(){
        return null;
    }

    public void clearGameData(){
    }
}
