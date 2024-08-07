package websocket.messages;

public class NotificationMessage() extends ServerMessage {

    public NotificationMessage() {
        this.serverMessageType = ServerMessageType.NOTIFICATION;
    }
}
