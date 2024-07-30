package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class SQLGameDAO implements GameDAO{
    private final DatabaseManager databaseManager;

    public SQLGameDAO() throws DataAccessException{
        this.databaseManager = new DatabaseManager();
    }

    public int createGame(String name) throws DataAccessException {
        int gameID = UUID.randomUUID().hashCode();
        if (gameID < 0){
            gameID = -gameID;
        }
        ChessGame game = new ChessGame();
        var gameJSON = new Gson().toJson(game, ChessGame.class);
        var statement = databaseManager.setDB("INSERT INTO %DB_NAME%.gameData (gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)");
        databaseManager.executeUpdate(statement, gameID, null, null, name, gameJSON);
        return gameID;
    }

    private GameData readGameData(ResultSet rs) throws SQLException {
        var gameID = rs.getInt("gameID");
        var whiteUsername = rs.getString("whiteUsername");
        var blackUsername = rs.getString("blackUsername");
        var gameName = rs.getString("gameName");
        String gameJSON = rs.getString("game");
        ChessGame game = new Gson().fromJson(gameJSON, ChessGame.class);
        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
    }

    private Collection<GameData> getGameDataCollection() throws DataAccessException {
        Collection<GameData> games = new ArrayList<>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = databaseManager.setDB("SELECT gameID, whiteUsername, blackUsername, gameName, game FROM %DB_NAME%.gameData");
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        games.add(readGameData(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return games;
    }

    public GameData getGame(int gameID) throws DataAccessException {
        Collection<GameData> games = getGameDataCollection();
        for (GameData gameData : games) {
            if (gameData.gameID() == gameID) {
                return gameData;
            }
        }
        return null;
    }

    public void updateGame(String name, ChessGame.TeamColor playerColor, GameData gameData){
    }

    public Collection<GameData> listGames(){
        return null;
    }

    public void clearGameData() throws DataAccessException {
        var statement = databaseManager.setDB("TRUNCATE %DB_NAME%.gameData");
        databaseManager.executeUpdate(statement);
    }
}
