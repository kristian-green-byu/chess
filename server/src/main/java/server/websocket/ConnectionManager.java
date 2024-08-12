package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import websocket.messages.ServerMessage;
import com.google.gson.Gson;


public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String username, Session session, int gameID){
        Connection connection = new Connection(username, session, gameID);
        connections.put(username, connection);
    }

    public void remove(String username){
        connections.remove(username);
    }

    public void broadcast(String excludeUsername, ServerMessage serverMessage, int gameID) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (!c.username.equals(excludeUsername) && gameID==c.gameID) {
                    c.send(new Gson().toJson(serverMessage));
                }
            } else {
                removeList.add(c);
            }
        }
        for (var c : removeList) {
            connections.remove(c.username);
        }
    }

    public void sendMessageToUser(String username, ServerMessage serverMessage) throws IOException {
        for (var c : connections.values()) {
            if(c.username.equals(username)){
                if(c.session.isOpen()){
                    c.send(new Gson().toJson(serverMessage));
                }
            }
        }
    }
}
