package ui;

import java.util.Arrays;
import java.util.Collection;

import java.util.Scanner;

import javax.management.Notification;

import com.google.gson.Gson;

import chess.ChessGame;
import exception.ResponseException;
import model.*;
import model.res.*;
import server.ServerFacade;

import static ui.EscapeSequences.*;
import ui.BoardPrinter;   

public class ChessClient {
    private String visitorName = null;
    private String authToken = null;
    private final ServerFacade server;
    private State state = State.SIGNEDOUT;
    private boolean inGame = false;
    private int gameID;
    private String colorPlayer;


    public ChessClient(String serverUrl) throws ResponseException {
        server = new ServerFacade(serverUrl);
    }

    public void run() {
        System.out.println(" Welcome to Chess. Type help to get started");
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();            
            try {
                result = eval(line);
                System.out.print(result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        String prefix = (state == State.SIGNEDIN) ? "[LOGGED_IN]" : "[LOGGED_OUT]";
        System.out.print("\n" + prefix + ">>> ");
    }


    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "logout" -> logout();
                case "list" -> listChess();
                case "create" -> createGame(params);
                case "join" -> joinGame(params);
                case "redraw" -> redraw();
                case "observe" -> observeGame(params);
                case "help" -> help();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String login(String... params) throws ResponseException {
        if (params.length >= 2) {
            String username = params[0];
            String password = params[1];
            state = State.SIGNEDIN;
            AuthData auth = server.login(username, password);
            visitorName = auth.username();
            authToken = auth.authToken();
            return String.format("You signed in as %s.", visitorName);
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: login <username> <password>");
    }

    public String register(String... params) throws ResponseException {
        if (params.length >= 3) {
            String username = params[0];
            String password = params[1];
            String email = params[2];
            state = State.SIGNEDIN;
            AuthData auth = server.register(username, password, email);
            visitorName = auth.username();
            authToken = auth.authToken();
            return String.format("Your registered as %s.", visitorName);
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <username> <password> <email>");
    }

    public String listChess() throws ResponseException {
        assertSignedIn();
        var result = server.listGames(authToken);

        return result;
    }

    public String logout() throws ResponseException {
        assertSignedIn();
        server.logout(authToken);
        state = State.SIGNEDOUT;
        return String.format("Logged out");
    }

    public String createGame(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length == 1) {
            String gameName = params[0];
    
            CreateResult res = server.createGame(authToken, gameName);

            return String.format("game created, id: %d", res.gameID());
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <gameName>");
    }

    public String joinGame(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length == 2) {
            int id = Integer.parseInt(params[0]);
            String color = params[1];
    
            ChessGame game = server.joinGame(authToken, color, id);

            inGame = true;
            colorPlayer = color;
            gameID = id;

            BoardPrinter board = new BoardPrinter();

            board.drawBoard(game, color);

            return String.format("");
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <gameId> [WHITE|BLACK]");
    }

    public String redraw() throws ResponseException {
        assertSignedIn();
        assertInGame();
    
            ChessGame game = server.redrawGame(authToken, gameID);

            BoardPrinter board = new BoardPrinter();

            board.drawBoard(game, colorPlayer);

            return String.format("");
    }

    public String observeGame(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length == 1) {
            int id = Integer.parseInt(params[0]);
    
            inGame = true;
            colorPlayer = null;
            gameID = id;

            ChessGame game = server.observeGame(authToken, gameID);

            BoardPrinter board = new BoardPrinter();

            board.drawBoard(game, "white");

            return String.format("");
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <gameId> [WHITE|BLACK]");
    }

    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    - help
                    - register <username> <password> <email>
                    - login <username> <password>
                    - quit
                    """;
        }
        return """
                - help
                - list
                - create <gameName>
                - join <gameId> <WHITE|BLACK>
                - observe <gameId>
                - quit
                """;
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.SIGNEDOUT) {
            throw new ResponseException(ResponseException.Code.ClientError, "You must sign in");
        }
    }

    private void assertInGame() throws ResponseException {
        if (!inGame) {
            throw new ResponseException(ResponseException.Code.ClientError, "You must be in a game");
        }
    }
}