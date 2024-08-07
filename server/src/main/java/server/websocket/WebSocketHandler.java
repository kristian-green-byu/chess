package server.websocket;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.SQLAuthDAO;
import model.AuthData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import websocket.messages.NotificationMessage;

import java.io.IOException;



@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final AuthDAO authDAO = new SQLAuthDAO();

    public WebSocketHandler() throws DataAccessException {
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        String username = getUsername(command.getAuthToken());
        switch (command.getCommandType()) {
            case CONNECT -> enter(username, session);
            case LEAVE -> exit(command.getAuthToken());
        }
    }

    private String getUsername(String authToken) throws DataAccessException {
        AuthData userAuth = authDAO.getAuthData(authToken);
        return userAuth.username();
    }

    private void enter(String authToken, Session session) throws IOException, DataAccessException {
        String username = getUsername(authToken);
        connections.add(username, session);
        var message = String.format("%s joined the game", username);
        var notification = new NotificationMessage(message);
        connections.broadcast(username, notification);
    }

    private void exit(String authToken) throws IOException, DataAccessException {
        String username = getUsername(authToken);
        connections.remove(username);
        var message = String.format("%s left the game", username);
        var notification = new NotificationMessage(message);
        connections.broadcast(username, notification);
    }
}
