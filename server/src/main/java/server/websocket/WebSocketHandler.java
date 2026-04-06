package server.websocket;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.AuthData;
import model.GameData;
import model.SocketData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
import websocket.messages.*;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final ConnectionManager connections;
    private final Gson gson = new Gson();

    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
        this.connections = new ConnectionManager();
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        try {
            UserGameCommand command = gson.fromJson(ctx.message(), UserGameCommand.class);
            if (command == null || command.getCommandType() == null) {
                sendError(ctx.session, "Error: bad command");
                return;
            }

            switch (command.getCommandType()) {
                case CONNECT -> connect(command, ctx.session);
                case LEAVE -> leave(command, ctx.session); // Change this line
                default -> sendError(ctx.session, "Error: unsupported command");
            }
        } catch (Exception ex) {
            try {
                sendError(ctx.session, "Error: " + ex.getMessage());
            } catch (IOException ignored) {
            }
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) throws Exception {
        SocketData data = connections.get(ctx.session);
        if (data != null) {
            String username = data.username();
            Integer gameID = data.gameID();
            
            connections.remove(ctx.session);
        System.out.println("user left");

            var message = String.format("%s has left the game", username);
            var notification = new NotificationMessage(message);
            connections.broadcastInGame(gameID, ctx.session, notification);
        }
    }

    private void leave(UserGameCommand command, Session session) throws IOException, DataAccessException {
        AuthData auth = authDAO.getAuth(command.getAuthToken());

        if (auth == null) {
            sendError(session, "Error: unauthorized");
            return;
        }
        
        System.out.println("user left");

        Integer gameID = command.getGameID();
        String username = auth.username();

        connections.remove(session);

        var message = String.format("%s has left the game", username);
        var notification = new NotificationMessage(message);
        connections.broadcastInGame(gameID, session, notification);
    }

    private void connect(UserGameCommand command, Session session) throws IOException, DataAccessException {
        AuthData auth = authDAO.getAuth(command.getAuthToken());

        if (auth == null) {
            sendError(session, "Error: unauthorized");
            return;
        }

        Integer gameID = command.getGameID();

        System.out.println("user joined");

        GameData game = gameDAO.getGame(gameID);

        if (game == null) {
            sendError(session, "Error: bad gameID");
            return;
        }

        connections.add(session, new SocketData(auth.username(), command.getAuthToken(), gameID));

        connections.send(session, new LoadGameMessage(game.game()));

        String msg = auth.username() + " joined the game";
        connections.broadcastInGame(gameID, session, new NotificationMessage(msg));
    }

    private void sendError(Session session, String message) throws IOException {
        connections.send(session, new ErrorMessage(message));
    }
}