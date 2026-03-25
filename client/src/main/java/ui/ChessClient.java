package ui;

import java.util.Arrays;
import java.util.Collection;

import java.util.Scanner;

import javax.management.Notification;

import com.google.gson.Gson;

import exception.ResponseException;
import model.*;
import model.res.*;
import server.ServerFacade;

public class ChessClient {
    private String visitorName = null;
    private String authToken = null;
    private final ServerFacade server;
    private State state = State.SIGNEDOUT;

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


    public void notify(Notification notification) {
        System.out.println(RED + notification.message());
        printPrompt();
    }

    private void printPrompt() {
        System.out.print("\n" + RESET + ">>> " + GREEN);
    }


    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "logout" -> logout(params);
                case "list" -> listChess();
                case "create" -> createGame(params);
                case "join" -> joinGame(params);
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
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <username> <password>");
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
        ListResult games = server.listGames(authToken);
        var result = new StringBuilder();
        var gson = new Gson();
        for (GameData game : games.games()) {
            result.append(gson.toJson(game)).append('\n');
        }
        return result.toString();
    }

    public String adoptPet(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length == 1) {
            try {
                int id = Integer.parseInt(params[0]);
                Pet pet = getPet(id);
                if (pet != null) {
                    server.deletePet(id);
                    return String.format("%s says %s", pet.name(), pet.sound());
                }
            } catch (NumberFormatException ignored) {
            }
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <pet id>");
    }

    public String adoptAllPets() throws ResponseException {
        assertSignedIn();
        var buffer = new StringBuilder();
        for (Pet pet : server.listPets()) {
            buffer.append(String.format("%s says %s%n", pet.name(), pet.sound()));
        }

        server.deleteAllPets();
        return buffer.toString();
    }

    public String signOut() throws ResponseException {
        assertSignedIn();
        ws.leavePetShop(visitorName);
        state = State.SIGNEDOUT;
        return String.format("%s left the shop", visitorName);
    }

    private Pet getPet(int id) throws ResponseException {
        for (Pet pet : server.listPets()) {
            if (pet.id() == id) {
                return pet;
            }
        }
        return null;
    }

    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    - signIn <yourname>
                    - quit
                    """;
        }
        return """
                - list
                - adopt <pet id>
                - rescue <name> <CAT|DOG|FROG|FISH>
                - adoptAll
                - signOut
                - quit
                """;
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.SIGNEDOUT) {
            throw new ResponseException(ResponseException.Code.ClientError, "You must sign in");
        }
    }
}