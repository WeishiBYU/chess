package client.websocket;

import server.webSocketMessages.*;

public interface NotificationHandler {
    void notify(Notification notification);

}