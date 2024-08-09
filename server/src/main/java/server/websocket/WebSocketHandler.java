package server.websocket;

import com.google.gson.Gson;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;



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
        }
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
            var error = new ErrorMessage("Error: Requested game doesn't exist");
            connections.sendMessageToUser(username, error);
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
