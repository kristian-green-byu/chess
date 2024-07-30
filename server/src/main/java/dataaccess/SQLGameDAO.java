package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class SQLGameDAO implements GameDAO{
    private final DatabaseManager db;

    public SQLGameDAO() throws DataAccessException{
        this.db = new DatabaseManager();
    }

    public int createGame(String name) throws DataAccessException {
        int gameID = UUID.randomUUID().hashCode();
        if (gameID < 0){
            gameID = -gameID;
        }
        ChessGame game = new ChessGame();
        var gameJSON = new Gson().toJson(game, ChessGame.class);
        var statement = db.setDB("INSERT INTO %DB_NAME%.gameData (gameID, gameName, game) VALUES (?, ?, ?)");
        db.executeUpdate(statement, gameID, name, gameJSON);
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
            var statement = db.setDB("SELECT gameID, whiteUsername, blackUsername, gameName, game FROM %DB_NAME%.gameData");
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
        if (gameID < 0){
            throw new DataAccessException("unauthorized");
        }
        Collection<GameData> games = getGameDataCollection();
        for (GameData gameData : games) {
            if (gameData.gameID() == gameID) {
                return gameData;
            }
        }
        return null;
    }

    public void updateGame(String name, ChessGame.TeamColor playerColor, GameData gameData) throws DataAccessException {
        if(name == null || gameData == null || playerColor == null){
            throw new DataAccessException("unauthorized");
        }
        Collection<GameData> games = getGameDataCollection();
        GameData newGame;
        if (playerColor == ChessGame.TeamColor.WHITE) {
            newGame = new GameData(gameData.gameID(), name, gameData.blackUsername(), gameData.gameName(), gameData.game());
        } else {
            newGame = new GameData(gameData.gameID(), gameData.whiteUsername(), name, gameData.gameName(), gameData.game());
        }
        for (GameData game : games) {
            if(game.gameID() == gameData.gameID()) {
                var statement = db.setDB("DELETE FROM %DB_NAME%.gameData WHERE gameID = ?");
                db.executeUpdate(statement, gameData.gameID());
                var s2 = db.setDB("INSERT INTO %DB_NAME%.gameData (gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)");
                var gameJSON = new Gson().toJson(newGame.game(), ChessGame.class);
                db.executeUpdate(s2, newGame.gameID(), newGame.whiteUsername(), newGame.blackUsername(), newGame.gameName(),gameJSON);
            }
        }
    }

    public Collection<GameData> listGames() throws DataAccessException {
        return getGameDataCollection();
    }

    public void clearGameData() throws DataAccessException {
        var statement = db.setDB("TRUNCATE %DB_NAME%.gameData");
        db.executeUpdate(statement);
    }
}
