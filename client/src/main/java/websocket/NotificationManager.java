package websocket;

import websocket.messages.NotificationMessage;

public interface NotificationManager {
    void notify(NotificationMessage notification);
}
