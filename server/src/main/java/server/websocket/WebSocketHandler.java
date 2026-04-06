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
    public void handleClose(WsCloseContext ctx) {
        connections.remove(ctx.session);
    }

    private void connect(UserGameCommand command, Session session) throws IOException, DataAccessException {
        AuthData auth = authDAO.getAuth(command.getAuthToken());

        if (auth == null) {
            sendError(session, "Error: unauthorized");
            return;
        }

        Integer gameID = command.getGameID();

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