package ui;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import chess.ChessGame;
import client.websocket.*;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.res.CreateResult;
import server.ServerFacade;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;   

public class ChessClient implements NotificationHandler {
    private String visitorName = null;
    private String authToken = null;
    private final ServerFacade server;
    private State state = State.SIGNEDOUT;
    private int gameID;
    private String colorPlayer;
    private WebSocketFacade ws;
    private String serverUrl;

    Map<Integer, GameData> games = new HashMap<Integer,GameData>();


    public ChessClient(String serverUrl) throws ResponseException {
        this.serverUrl = serverUrl;
        server = new ServerFacade(serverUrl);
    }

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> {
                LoadGameMessage loadGameMessage = (LoadGameMessage) message;
                BoardPrinter board = new BoardPrinter();
                board.drawBoard(loadGameMessage.getGame(), colorPlayer);
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
        String prefix = switch (state) {
            case SIGNEDOUT -> "[LOGGED_OUT]";
            case SIGNEDIN -> "[LOGGED_IN]";
            case INGAME -> "[" + (colorPlayer != null ? colorPlayer.toUpperCase() : "OBSERVER") + "]";
        };
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
        if (params.length == 2) {
            String username = params[0];
            String password = params[1];

            if (state == State.SIGNEDIN) {
                throw new ResponseException(ResponseException.Code.ClientError, "Can't Double Login");
            }

            AuthData auth = server.login(username, password);
            visitorName = auth.username();
            authToken = auth.authToken();
            state = State.SIGNEDIN;

            ws = new WebSocketFacade(serverUrl, this);

            return String.format("You signed in as %s.", visitorName);
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: login <username> <password>");
    }

    public String register(String... params) throws ResponseException {
        if (params.length >= 3) {
            String username = params[0];
            String password = params[1];
            String email = params[2];

            AuthData auth = server.register(username, password, email);

            state = State.SIGNEDIN;

            visitorName = auth.username();
            authToken = auth.authToken();
            return String.format("Your registered as %s.", visitorName);
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <username> <password> <email>");
    }

    public String listChess() throws ResponseException {
        assertSignedIn();
        games = server.listGames(authToken);

        String result = "no games"; 

        for (int i = 1; i <= games.size(); i++) {
            result = "";

            GameData game = games.get(i);

            String white = (game.whiteUsername() == null) ? "" : game.whiteUsername();

            String black = (game.blackUsername() == null) ? "" : game.blackUsername();


            System.out.print(String.format("%d. %s %nWhite: %s %nBlack: %s%n", i, game.gameName(), white, black));
        }

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

            return String.format("game created");
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <gameName>");
    }

    public String joinGame(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length == 2) {
            try {
            int id = Integer.parseInt(params[0]);
            String color = params[1];


            if (!color.equals("white") && !color.equals("black")) {
                throw new ResponseException(ResponseException.Code.ClientError, "Expected colors: [WHITE|BLACK]");
            }

            if (id > games.size() || id < 1) {
                throw new ResponseException(ResponseException.Code.ClientError, "Can't find game with that number");
            }
            
            GameData gameData = games.get(id);

            gameID = gameData.gameID();


            ChessGame game = server.joinGame(authToken, color, gameID);

            state = State.INGAME;
            colorPlayer = color;

            ws.Connect(authToken, gameID);

            BoardPrinter board = new BoardPrinter();

            board.drawBoard(game, color);
            }

            catch(NumberFormatException e) {
                throw new ResponseException(ResponseException.Code.ClientError, "Expected: <gameId> as number");
            }


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
            try {

            int id = Integer.parseInt(params[0]);

            if (id > games.size() || id < 1) {
                throw new ResponseException(ResponseException.Code.ClientError, "Can't find game with that number");
            }

            state = State.INGAME;
            colorPlayer = null;
            gameID = id;

            ws.Connect(authToken, gameID);

            ChessGame game = server.observeGame(authToken, gameID);

            BoardPrinter board = new BoardPrinter();

            board.drawBoard(game, "white");

            return String.format("");

            }

            catch(NumberFormatException e) {
                throw new ResponseException(ResponseException.Code.ClientError, "Expected: <gameId> as number");
            }
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <gameId>");
    }

    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    - help
                    - register <username> <password> <email>
                    - login <username> <password>
                    - quit
                    """;
        } else if (state == State.SIGNEDIN) {
            return """
                - help
                - list
                - create <gameName>
                - logout
                - join <gameId> <WHITE|BLACK>
                - observe <gameId>
                - quit
                """;
        }
        return """
            - help
            - redraw
            - leave
            - move <starting position (ex. e2)> <ending position (e4)>
            - resign
            - legalMoves <starting position (ex. e2)>
            """;

    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.SIGNEDOUT) {
            throw new ResponseException(ResponseException.Code.ClientError, "You must sign in");
        } else if (state == State.INGAME) {
            throw new ResponseException(ResponseException.Code.ClientError, "You must sign in");
        }
    }

    private void assertInGame() throws ResponseException {
        if (state != State.INGAME) {
            throw new ResponseException(ResponseException.Code.ClientError, "You must be in a game");
        }
    }
}