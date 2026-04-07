package ui;

import java.util.Collection;
import java.util.Scanner;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
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
                if (playerColor == null) {
                    board.drawBoard(game, "white");
                } else {
                    board.drawBoard(this.game, playerColor);
                }

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
                System.out.print(result);
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
            case "move" -> move(params);
            case "resign" -> resign();
            case "leave" -> leave();
            case "redraw" -> redraw();
            case "help" -> help();
            case "check" -> check(params);
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

        if (playerColor == null) {
            board.drawBoard(game, "white");
            return "";
        }

        board.drawBoard(game, playerColor);
        return "";
    }

    private String resign() throws ResponseException {

        if (playerColor == null) {
            return "Observers cannot resign.";
        }

        Scanner scan = new Scanner(System.in);

        System.out.println("Are you sure you want to resign? [y/n]: ");
        String answer = scan.nextLine();

        if(answer.equals("y")) {
            ws.resign(authToken, gameID);
        }

        return "";
    }

    private String check(String... params) throws ResponseException {
        if (params.length == 1) {
            String pos = params[0];


            ChessPosition piecePos = posFinder(pos);

            if (piecePos == null) {
            return "Invalid position format. Use algebraic notation (e.g., 'a2').";
            }              

            BoardPrinter board = new BoardPrinter();

            Collection<ChessMove> moves = game.validMoves(piecePos);

            if (moves != null && playerColor != null) {
                board.drawMoves(game, playerColor, moves);
            } else if (moves != null && playerColor == null){
                board.drawMoves(game, "white", moves);
            } else {
                throw new ResponseException(ResponseException.Code.ClientError, "no piece found in position");
            }

            return "";
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: move <from> <to> [promotion]  (e.g., move e2 e4)");
    }

    private String move(String... params) throws ResponseException {
        if (params.length == 2 || params.length == 3) {
            String start = params[0];
            String end = params[1];
            ChessPiece.PieceType promotion = (params.length > 2) ? promtion(params[2]) : null;

            ChessPosition startPos = posFinder(start);
            ChessPosition endPos = posFinder(end);

            if (startPos == null || endPos == null) {
            return "Invalid position format. Use algebraic notation (e.g., 'a2').";
            }              

            ChessMove move = new ChessMove(startPos, endPos, promotion);
            
            ws.makeMove(authToken, gameID, move);

            return "Move command sent to the server.";
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: move <from> <to> [promotion]  (e.g., move e2 e4 or move e7 e8 rook)");
    }

    private String help() {
        if (playerColor == null) {
            return """
                - redraw
                - leave
                - help
                - redraw
                - check <piece position> e.g., check e2
                """;
        }
        return """
            - redraw
            - leave
            - move <from> <to> [promotion]  (e.g., move e2 e4)
            - resign
            - help
            - check <piece position> e.g., check e2
            """;
    }

    private ChessPosition posFinder(String pos) {
        if (pos.length() != 2) {
            return null;
        }

        char colChar = pos.charAt(0);
        char rowChar = pos.charAt(1);

        if (colChar < 'a' || colChar > 'h' || rowChar < '1' || rowChar > '8') {
            return null;
        }

        int col = colChar - 'a' + 1;
        int row = Character.getNumericValue(rowChar);

        return new ChessPosition(row, col);
    }

    private ChessPiece.PieceType promtion(String piece) {
        piece = piece.toUpperCase();

        ChessPiece.PieceType p = switch (piece) {
            case "KING" -> ChessPiece.PieceType.KING;
            case "QUEEN"  -> ChessPiece.PieceType.QUEEN;
            case "BISHOP" -> ChessPiece.PieceType.BISHOP;
            case "KNIGHT" -> ChessPiece.PieceType.KNIGHT;
            case "ROOK"   -> ChessPiece.PieceType.ROOK;
            default -> null;
        };
                

        return p;
    }
}