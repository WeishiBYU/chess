package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import model.*;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Session, SocketData> connections = new ConcurrentHashMap<>();

    public void add(Session session, SocketData data) {
        connections.put(session, data);
    }

    public void remove(Session session) {
        connections.remove(session);
    }

    public void broadcast(Session excludeSession, ServerMessage notification) throws IOException {
        String msg = notification.toString();
        for (Session c : connections.keySet()) {
            if (c.isOpen()) {
                if (!c.equals(excludeSession)) {
                    c.getRemote().sendString(msg);
                }
            }
        }
    }

    public void send(Session session, ServerMessage notification) throws IOException {
        String msg = notification.toString();
        for (Session c : connections.keySet()) {
            if (c.isOpen()) {
                if (c.equals(session)) {
                    c.getRemote().sendString(msg);
                }
            }
        }
    }
}