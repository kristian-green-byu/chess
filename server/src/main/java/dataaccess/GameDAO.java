package dataaccess;
import model.GameData;

public interface GameDAO {
    int createGame(String name);
    GameData getGame(int gameID);
}
