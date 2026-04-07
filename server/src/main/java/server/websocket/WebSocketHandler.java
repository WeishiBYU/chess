package server.websocket;

import com.google.gson.Gson;

import chess.ChessGame.TeamColor;
import chess.InvalidMoveException;
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
import websocket.commands.*;
import websocket.messages.*;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final ConnectionManager connections;
    private final Gson gson = new Gson();
    TeamColor turn;

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
                case LEAVE -> leave(command, ctx.session);
                case RESIGN -> resign(command, ctx.session);
                case MAKE_MOVE -> {
                    MakeMoveCommand makeMoveCommand = gson.fromJson(ctx.message(), MakeMoveCommand.class);
                    makeMove(makeMoveCommand, ctx.session);
                }
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
        connections.remove(ctx.session);
        System.out.println("user left");
    }

    private void leave(UserGameCommand command, Session session) throws IOException, DataAccessException {
        AuthData auth = authDAO.getAuth(command.getAuthToken());

        if (auth == null) {
            sendError(session, "Error: unauthorized");
            return;
        }
        Integer gameID = command.getGameID();

        System.out.println("user left");

        String username = auth.username();
        String playerColor = null;

        GameData gameData = gameDAO.getGame(gameID);
        if (gameData == null) {
            sendError(session, "Error: Invalid game ID");
            return;
        }
        
        if (username.equals(gameData.whiteUsername())) {
            playerColor = "WHITE";
        } else if (username.equals(gameData.blackUsername())) {
            playerColor = "BLACK";
        }

        if (playerColor != null) {
            if (playerColor.equals("WHITE")) {
                gameData = new GameData(gameID, null, gameData.blackUsername(), gameData.gameName(), gameData.game());
            } else {
                gameData = new GameData(gameID, gameData.whiteUsername(), null, gameData.gameName(), gameData.game());
            }
            gameDAO.updateGame(gameData);
        }

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

        var message = String.format("%s has joined the game", auth.username());
        var notification = new NotificationMessage(message);
        connections.broadcastInGame(gameID, session, notification);

        connections.send(session, new LoadGameMessage(game.game()));
    }

    private void resign(UserGameCommand command, Session session) throws IOException, DataAccessException {
        AuthData auth = authDAO.getAuth(command.getAuthToken());

        Integer gameID = command.getGameID();
        GameData game = gameDAO.getGame(gameID);

        String username = auth.username();
        String playerColor = null;

        if (game.game().isGameOver()) {
            sendError(session, "Game is over");
            return;
        }

        if (username.equals(game.whiteUsername())) {
            playerColor = "WHITE";
        } else if (username.equals(game.blackUsername())) {
            playerColor = "BLACK";
        }

        if (auth == null || playerColor == null) {
            sendError(session, "Error: unauthorized");
            return;
        }
        
        game.game().setGameOver();

        gameDAO.updateGame(game);
        
        System.out.println("user resigned");


        var message = String.format("%s has resigned", username);
        var notification = new NotificationMessage(message);
        connections.broadcastInGame(gameID, null, notification);
    }

    private void makeMove(MakeMoveCommand command, Session session) throws IOException, DataAccessException {
        AuthData auth = authDAO.getAuth(command.getAuthToken());
        TeamColor playerColor = null;
        Integer gameID = command.getGameID();
        GameData gameData = gameDAO.getGame(gameID);
        
            if (gameData.game().isGameOver()) {
                sendError(session, "Game is over");
                return;
            }

        if (auth == null) {
            sendError(session, "Error: unauthorized");
            return;
        }

        turn = gameData.game().getTeamTurn();

        if (gameData.whiteUsername() != null && gameData.whiteUsername().equals(auth.username())) {
            playerColor = TeamColor.WHITE;
        } else if (gameData.blackUsername() != null && gameData.blackUsername().equals(auth.username())) {
            playerColor = TeamColor.BLACK;
        }
        
        if (playerColor == null || !(playerColor.equals(turn))) {
            sendError(session, "It's not your turn");
            return;
        }

        System.out.println("move ready");

        try {
            gameData.game().makeMove(command.getMove());
            
            TeamColor newColor = (turn.equals(TeamColor.WHITE)) ? TeamColor.BLACK : TeamColor.WHITE;

            gameData.game().setTeamTurn(newColor);
        } catch (InvalidMoveException e) {
            sendError(session, "Error: " + e.getMessage());
            return;
        }

        gameDAO.updateGame(gameData);
        var notification = new NotificationMessage(auth.username() + " made a move.");
        connections.broadcastInGame(gameID, session, notification);

        var loadGameMessage = new LoadGameMessage(gameData.game());
        connections.broadcastInGame(gameID, null, loadGameMessage);

        TeamColor opponentTeam = (playerColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;

        if (gameData.game().isInCheckmate(opponentTeam)) {
            gameData.game().setGameOver();
            gameDAO.updateGame(gameData);
            var checkmateMsg = new NotificationMessage(opponentTeam + " is in checkmate. " + playerColor + " wins!");
            connections.broadcastInGame(gameID, null, checkmateMsg);
        } else if (gameData.game().isInStalemate(opponentTeam)) {
            gameData.game().setGameOver();
            gameDAO.updateGame(gameData);
            var stalemateMsg = new NotificationMessage("The game is a stalemate.");
            connections.broadcastInGame(gameID, null, stalemateMsg);
        } else if (gameData.game().isInCheck(opponentTeam)) {
            var checkMsg = new NotificationMessage(opponentTeam + " is in check.");
            connections.broadcastInGame(gameID, null, checkMsg);
        }
    }

    private void sendError(Session session, String message) throws IOException {
        connections.send(session, new ErrorMessage(message));
    }

}