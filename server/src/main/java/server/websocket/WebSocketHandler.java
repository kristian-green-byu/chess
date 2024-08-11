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
        String username = getUsername(command.getAuthToken());
        if(username==null){
            ServerMessage error = new ErrorMessage("Error: User not logged in");
            session.getRemote().sendString(new Gson().toJson(error));
            return;
        }
        Integer gameID = command.getGameID();
        switch (command.getCommandType()) {
            case CONNECT -> connect(username, session, gameID);
            case LEAVE -> leave(username);
            case MAKE_MOVE ->handleMakeMove(command, username, session, gameID);
        }
    }

    private void handleMakeMove(UserGameCommand command, String username, Session session, int gameID) throws IOException, DataAccessException {
        MakeMoveCommand makeMove = (MakeMoveCommand) command;
        makeMove(username, session, gameID, makeMove.getMove());
    }

    private void makeMove(String username, Session session, Integer gameID, ChessMove move) throws IOException, DataAccessException {
        GameData game = gameDAO.getGame(gameID);
        if(game==null){
            gameNoExistError(username);
            return;
        }
        //check to make sure that user is in game
        ChessGame.TeamColor moveColor = game.game().getBoard().getPiece(move.getStartPosition()).getTeamColor();
        if(moveColor==ChessGame.TeamColor.BLACK){
            if(!Objects.equals(game.blackUsername(), username)){
                sendUserNotInGameError(username);
                return;
            }
        }
        else{
            if(!Objects.equals(game.whiteUsername(), username)){
                sendUserNotInGameError(username);
                return;
            }
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
        gameDAO.updateGame(game.gameName(), moveColor, game);
        var loadGame = new LoadGameMessage(game);
        connections.broadcast("", loadGame);
        //String notifyString = String.format("%s has moved from %s to %s.", username, )
        //var moveNotify = new NotificationMessage()

    }

    private void sendInvalidMoveError(String username) throws IOException {
        var error = new ErrorMessage("Error: Move not possible");
        connections.sendMessageToUser(username, error);
        return;
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

    private void connect(String username, Session session, Integer gameID) throws IOException, DataAccessException {
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

    private void leave(String username) throws IOException {
        connections.remove(username);
        var message = String.format("%s left the game", username);
        var notification = new NotificationMessage(message);
        connections.broadcast(null, notification);
    }
}
