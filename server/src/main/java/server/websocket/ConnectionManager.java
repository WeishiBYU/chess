package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import model.*;
import com.google.gson.Gson;


import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Session, SocketData> connections = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();

    public void add(Session session, SocketData data) {
        connections.put(session, data);
    }
    
    public SocketData get(Session session) {
        return connections.get(session);
    }
    public void remove(Session session) {
        connections.remove(session);
    }

    public void broadcastInGame(Integer gameID, Session excludeSession, ServerMessage notification) throws IOException {
        String msg = gson.toJson(notification);
        for (var entry : connections.entrySet()) {
            Session c = entry.getKey();
            SocketData data = entry.getValue();

            if (c.isOpen()
                    && !c.equals(excludeSession)
                    && gameID.equals(data.gameID())) {
                c.getRemote().sendString(msg);
            }
        }
    }

    public void send(Session session, ServerMessage notification) throws IOException {
        String msg = gson.toJson(notification);
        if (session.isOpen()) {
            session.getRemote().sendString(msg);
        }
    }
}