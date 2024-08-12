package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Collection;
import java.util.Objects;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final AuthDAO authDAO = new SQLAuthDAO();
    private final GameDAO gameDAO = new SQLGameDAO();

    public WebSocketHandler() throws DataAccessException {
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> connect(command, session);
            case LEAVE -> leave(command, session);
            case MAKE_MOVE ->makeMove(message, session);
            case RESIGN -> resign(command, session);
        }
    }

    private void makeMove(String message, Session session) throws IOException, DataAccessException {
        MakeMoveCommand command = new Gson().fromJson(message, MakeMoveCommand.class);
        String username = getUsername(command.getAuthToken());
        if (userNotLoggedIn(session, username)){
            return;
        }
        Integer gameID = command.getGameID();
        ChessMove move = command.getMove();
        GameData game = gameDAO.getGame(gameID);
        if(game==null){
            gameNoExistError(username);
            return;
        }
        //check to make sure that user is in game
        ChessGame.TeamColor moveColor = game.game().getBoard().getPiece(move.getStartPosition()).getTeamColor();
        if (checkIfInGame(moveColor, game, username)){
            return;
        }
        if(checkIfGameOver(game, username)){
            return;
        }
        // validate move is in valid moves for requested start position
        Collection<ChessMove> validMoves = game.game().validMoves(move.getStartPosition());
        if(!validMoves.contains(move)){
            sendInvalidMoveError(username);
            return;
        }
        // check to make sure move is in turn
        if(game.game().getTeamTurn()!=moveColor){
            var error = new ErrorMessage("Error: Move out of turn");
            connections.sendMessageToUser(username, error);
            return;
        }
        try{
            game.game().makeMove(move);

        } catch(InvalidMoveException e){
            sendInvalidMoveError(username);
            return;
        }
        gameDAO.updateGame(username, moveColor, game);
        var loadGame = new LoadGameMessage(game);
        connections.broadcast("", loadGame);
        var moveNotify = getMoveNotification(username, move);
        connections.broadcast(username, moveNotify);

        //see if move put opponent in checkmate, stalemate, or check
        ChessGame.TeamColor opponentColor=ChessGame.TeamColor.BLACK;
        String opponentUsername = game.blackUsername();
        if(moveColor==ChessGame.TeamColor.BLACK){
            opponentColor=ChessGame.TeamColor.WHITE;
            opponentUsername=game.whiteUsername();
        }
        if(game.game().isInCheckmate(opponentColor)){
            String checkmateNotifyString = String.format("%s checkmated %s! Game over. %s won!", username, opponentUsername, username);
            var checkmateNotify = new NotificationMessage(checkmateNotifyString);
            connections.broadcast("", checkmateNotify);
            game.game().end();
        }
        else if(game.game().isInStalemate(opponentColor)){
            String checkmateNotifyString = String.format("%s and %s are in a stalemate! Game over.", username, opponentUsername);
            var checkmateNotify = new NotificationMessage(checkmateNotifyString);
            connections.broadcast("", checkmateNotify);
            game.game().end();
        }
        else if(game.game().isInCheck(opponentColor)){
            String checkmateNotifyString = String.format("%s put %s in check!", username, opponentUsername);
            var checkmateNotify = new NotificationMessage(checkmateNotifyString);
            connections.broadcast("", checkmateNotify);
        }
    }

    private static NotificationMessage getMoveNotification(String username, ChessMove move) {
        int startRow = move.getStartPosition().getRow();
        int startCol = move.getStartPosition().getColumn();
        String startCombo = makePositionOutput(startRow, startCol);
        int endRow = move.getEndPosition().getRow();
        int endCol = move.getEndPosition().getColumn();
        String endCombo = makePositionOutput(endRow, endCol);
        String notifyString = String.format("%s has moved from %s to %s.", username, startCombo, endCombo);
        return new NotificationMessage(notifyString);
    }

    private static String makePositionOutput(int row, int col) {
        char colChar = 'x';
        switch(col){
            case 1 -> colChar = 'a';
            case 2 -> colChar = 'b';
            case 3 -> colChar = 'c';
            case 4 -> colChar = 'd';
            case 5 -> colChar = 'e';
            case 6 -> colChar = 'f';
            case 7 -> colChar = 'g';
            case 8 -> colChar = 'h';
        }
        return colChar + Integer.toString(row);
    }

    private void sendInvalidMoveError(String username) throws IOException {
        var error = new ErrorMessage("Error: invalid move");
        connections.sendMessageToUser(username, error);
    }

    private void sendUserNotInGameError(String username) throws IOException {
        var error = new ErrorMessage("Error: User not in game");
        connections.sendMessageToUser(username, error);
    }

    private void gameNoExistError(String username) throws IOException {
        var error = new ErrorMessage("Error: Requested game doesn't exist");
        connections.sendMessageToUser(username, error);
    }

    private String getUsername(String authToken) throws DataAccessException {
        AuthData userAuth = authDAO.getAuthData(authToken);
        if(userAuth!=null){
            return userAuth.username();
        }
        return null;
    }

    private void connect(UserGameCommand command, Session session) throws IOException, DataAccessException {
        String username = getUsername(command.getAuthToken());
        if (userNotLoggedIn(session, username)){
            return;
        }
        Integer gameID = command.getGameID();
        connections.add(username, session);
        GameData game = gameDAO.getGame(gameID);
        if(game==null){
            gameNoExistError(username);
            return;
        }
        var loadGame = new LoadGameMessage(game);
        connections.sendMessageToUser(username, loadGame);
        var message = String.format("%s joined the game", username);
        var notification = new NotificationMessage(message);
        connections.broadcast(username, notification);
    }

    private void resign(UserGameCommand command, Session session) throws IOException, DataAccessException {
        String username = getUsername(command.getAuthToken());
        if (userNotLoggedIn(session, username)){
            return;
        }
        Integer gameID = command.getGameID();
        GameData game = gameDAO.getGame(gameID);
        if(game==null){
            gameNoExistError(username);
            return;
        }
        //check to make sure that user is in game
        ChessGame.TeamColor color = ChessGame.TeamColor.WHITE;
        if(Objects.equals(game.blackUsername(), username)){
            color = ChessGame.TeamColor.BLACK;
        }
        if (checkIfInGame(color, game, username)){
            return;
        }
        if(checkIfGameOver(game, username)){
            return;
        }
        game.game().end();
        gameDAO.updateGame(username, color, game);
        String otherUser = game.blackUsername();
        if(color.equals(ChessGame.TeamColor.BLACK)){
            otherUser = game.whiteUsername();
        }
        String resignMessage = String.format("%s has resigned. Game over. %s won!", username, otherUser);
        NotificationMessage notification = new NotificationMessage(resignMessage);
        connections.broadcast("", notification);
    }

    private boolean checkIfInGame(ChessGame.TeamColor color, GameData game, String username) throws IOException {
        if(color ==ChessGame.TeamColor.BLACK){
            if(!Objects.equals(game.blackUsername(), username)){
                sendUserNotInGameError(username);
                return true;
            }
        }
        else{
            if(!Objects.equals(game.whiteUsername(), username)){
                sendUserNotInGameError(username);
                return true;
            }
        }
        return false;
    }

    private boolean checkIfGameOver(GameData game, String username) throws IOException {
        if(game.game().isGameOver()){
            var error = new ErrorMessage("The game is over! You can no longer move.");
            connections.sendMessageToUser(username, error);
            return true;
        }
        return false;
    }

    private void leave(UserGameCommand command, Session session) throws IOException, DataAccessException {
        String username = getUsername(command, session);
        if (userNotLoggedIn(session, username)){
            return;
        }
        Integer gameID = command.getGameID();
        GameData game = gameDAO.getGame(gameID);
        if(game==null){
            gameNoExistError(username);
            return;
        }
        ChessGame.TeamColor color = getUserColor(game, username);
        GameData newGame = new GameData(gameID, game.whiteUsername(), null, game.gameName(), game.game());
        String otherUser = game.whiteUsername();
        ChessGame.TeamColor otherColor = ChessGame.TeamColor.WHITE;
        if(color==ChessGame.TeamColor.WHITE){
            newGame = new GameData(gameID, null, game.blackUsername(), game.gameName(), game.game());
            otherUser = game.blackUsername();
            otherColor = ChessGame.TeamColor.BLACK;
        }
        gameDAO.updateGame(otherUser, otherColor, newGame);
        connections.remove(username);
        var message = String.format("%s left the game", username);
        var notification = new NotificationMessage(message);
        connections.broadcast("", notification);
    }

    private static ChessGame.TeamColor getUserColor(GameData game, String username) {
        ChessGame.TeamColor color = ChessGame.TeamColor.WHITE;
        if(game.blackUsername().equals(username)){
            color = ChessGame.TeamColor.BLACK;
        }
        return color;
    }

    private static boolean userNotLoggedIn(Session session, String username) throws IOException {
        if(username ==null){
            ServerMessage error = new ErrorMessage("Error: User not logged in");
            session.getRemote().sendString(new Gson().toJson(error));
            return true;
        }
        return false;
    }

    private String getUsername(UserGameCommand command, Session session) throws DataAccessException, IOException {
        String username = getUsername(command.getAuthToken());
        if(username==null){
            ServerMessage error = new ErrorMessage("Error: User not logged in");
            session.getRemote().sendString(new Gson().toJson(error));
            return null;
        }
        return username;
    }
}
