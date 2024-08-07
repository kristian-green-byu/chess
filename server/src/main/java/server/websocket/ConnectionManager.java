package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import websocket.messages.ServerMessage;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<String, Connection>();

    public void add(String username, Session session){
        Connection connection = new Connection(username, session);
        connections.put(username, connection);
    }

    public void remove(String username){
        connections.remove(username);
    }

    public void broadcast(String excludeUsername, ServerMessage serverMessage) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (!c.username.equals(excludeUsername)) {
                    c.send(serverMessage.toString());
                }
            } else {
                removeList.add(c);
            }
        }
        for (var c : removeList) {
            connections.remove(c.username);
        }
    }
}
