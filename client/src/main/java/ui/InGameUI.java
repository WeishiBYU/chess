package ui;

import java.util.Scanner;

import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;
import exception.ResponseException;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

public class InGameUI implements NotificationHandler {

    private final WebSocketFacade ws;
    private final String authToken;
    private final int gameID;
    private String playerColor;
    private chess.ChessGame game;

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> {
                LoadGameMessage loadGameMessage = (LoadGameMessage) message;
                this.game = loadGameMessage.getGame();
                BoardPrinter board = new BoardPrinter();
                board.drawBoard(this.game, playerColor);
                System.out.print("\n" + "Board loaded.");
                printPrompt();
            }
            case NOTIFICATION -> {
                NotificationMessage notification = (NotificationMessage) message;
                System.out.print("\n" + notification.getMessage());
                printPrompt();
            }
            case ERROR -> {
                ErrorMessage error = (ErrorMessage) message;
                System.out.print("\n" + "Error: " + error.getErrorMessage());
                printPrompt();
            }
        }
    }

    public InGameUI(WebSocketFacade ws, String authToken, int gameID, String playerColor) {
        this.ws = ws;
        this.authToken = authToken;
        this.gameID = gameID;
        this.playerColor = playerColor;
    }

    public void run() throws ResponseException{
        System.out.println("You are now in the game. Type 'help' for commands.");

        ws.connect(authToken, gameID);

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("left")) {
            printPrompt();
            String line = scanner.nextLine();
            try {
                result = eval(line);
                // System.out.print(result);
            } catch (Exception e) {
                System.out.print(e.getMessage());
            }
        }
    }

    private void printPrompt() {
        String role = (playerColor != null) ? playerColor.toUpperCase() : "OBSERVER";
        System.out.print("\n[" + role + "] >>> ");
    }

    private String eval(String input) throws ResponseException {
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = java.util.Arrays.copyOfRange(tokens, 1, tokens.length);

        return switch (cmd) {
            // You will implement these methods to call ws.move(), ws.resign(), etc.
            // case "move" -> move(params);
            // case "resign" -> resign();
            case "leave" -> leave();
            case "redraw" -> redraw();
            case "help" -> help();
            default -> help();
        };
    }

    private String leave() throws ResponseException {
        ws.leave(authToken, gameID);

        System.out.println("You have left the game.");
        return "left";
    }

    private String redraw() {
        if (game == null) {
            return "The game board has not been loaded yet.";
        }
        BoardPrinter board = new BoardPrinter();
        board.drawBoard(game, playerColor);
        return "";
    }

    private String help() {
        return """
            - redraw
            - leave
            - move <from> <to> [promotion]  (e.g., move e2 e4)
            - resign
            - help
            """;
    }
}