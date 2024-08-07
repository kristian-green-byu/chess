package websocket;

import com.google.gson.Gson;
import websocket.messages.NotificationMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationManager notificationManager;

    public WebSocketFacade(String url) throws IOException {
        try {
            url = url.replace("http", "ws");
            URI wsUri = new URI(url + "/ws");
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, wsUri);
            this.session.addMessageHandler((MessageHandler.Whole<String>) message -> {
                NotificationMessage notification = new Gson().fromJson(message, NotificationMessage.class);
                notificationManager.notify(notification);
            });
        } catch (DeploymentException | URISyntaxException e) {
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }


}
