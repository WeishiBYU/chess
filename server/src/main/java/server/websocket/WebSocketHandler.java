package server.websocket;

import com.google.gson.Gson;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.MySQLAuthDAO;
import dataaccess.MySQLGameDAO;
import dataaccess.MySQLUserDAO;
import dataaccess.UserDAO;
import exception.ResponseException;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.SocketData;

import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;

import javax.management.Notification;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    private void webSocketHandler(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) throws Exception{
        this.userDAO = new MySQLUserDAO();
        this.authDAO = new MySQLAuthDAO();
        this.gameDAO = new MySQLGameDAO();
    }


    private final ConnectionManager connections = new ConnectionManager();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        try {
            UserGameCommand action = new Gson().fromJson(ctx.message(), UserGameCommand.class);

            
            SocketData data = new SocketData(action);
            switch (action.getCommandType()) {
                case CONNECT -> connect(action.getAuthToken(), ctx.session);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void connect(SocketData data, Session session) throws IOException {
        connections.add(session);
        var message = String.format("%s is in the shop", visitorName);
        var broadcast = new Notification(ServerMessage.serverMessageType.NOTIFICATION, message);
        connections.broadcast(session, notification);
    }
}