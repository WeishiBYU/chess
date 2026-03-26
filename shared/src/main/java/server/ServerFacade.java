package server;

import com.google.gson.Gson;

import chess.ChessGame;
import exception.ResponseException;
import model.*;
import model.requests.CreateRequest;
import model.requests.JoinRequest;
import model.requests.LoginRequest;
import model.requests.RegisterRequest;
import model.res.*;

import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public AuthData register(String username, String password, String email) throws ResponseException {
        var user = new RegisterRequest(username, password, email);
        var request = buildRequest("POST", "/user", user, null);
        var response = sendRequest(request);

        return handleResponse(response, AuthData.class);
    }

    public AuthData login(String username, String password) throws ResponseException {
        var user = new LoginRequest(username, password);
        var request = buildRequest("POST", "/session", user, null);
        var response = sendRequest(request);
        
        return handleResponse(response, AuthData.class);
    }

    public void logout(String authToken) throws ResponseException {
        var request = buildRequest("DELETE", "/session", null, authToken);

        var response = sendRequest(request);
        
        handleResponse(response, null);
    }

    public Map<Integer, GameData> listGames(String authToken) throws ResponseException {
        var request = buildRequest("GET", "/game", null, authToken);
        var response = sendRequest(request);
        ListResult games = handleResponse(response, ListResult.class);

        int count = 1;

        Map<Integer, GameData> list =  new HashMap<>();;

        for (GameData game : games.games()) {

            list.put(count, game);

            count++;
        }

        return list;
    }

    public CreateResult createGame(String authToken, String gameName) throws ResponseException {
        var body = new CreateRequest(gameName);
        var request = buildRequest("POST", "/game", body, authToken);
        var response = sendRequest(request);
        return handleResponse(response, CreateResult.class);
    }

    public ChessGame joinGame(String authToken, String playerColor, int gameID) throws ResponseException {
        playerColor = playerColor.toUpperCase();
        
        var body = new JoinRequest(playerColor, gameID);
        var request = buildRequest("PUT", "/game", body, authToken);
        var response = sendRequest(request);
        handleResponse(response, null);

        return getGame(authToken, gameID);
    }

    public ChessGame redrawGame(String authToken, int gameID) throws ResponseException {        
        return getGame(authToken, gameID);
    }

    public ChessGame getGame(String authToken, int gameID) throws ResponseException {
        var request = buildRequest("GET", "/game", null, authToken);
        var response = sendRequest(request);
        
        ListResult games = handleResponse(response, ListResult.class);

        for (GameData game : games.games()) {
            if (gameID == game.gameID()) {
                return game.game(); 
            }
        }
        
        return null;
    }

    
    public ChessGame observeGame(String authToken, int gameID) throws ResponseException {
        var response = getGame(authToken, gameID);

        return response;
    }

    public void clearDB() throws ResponseException {
        var request = buildRequest("DELETE", "/db", null, null);
        
        sendRequest(request);
    }

    private HttpRequest buildRequest(String method, String path, Object body, String header) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        } 
        
        if (header != null) {
            request.setHeader("authorization", header);
        }

        return request.build();
    }

    private BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException {
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ResponseException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw ResponseException.fromJson(body);
            }

            throw new ResponseException(ResponseException.fromHttpStatusCode(status), "other failure: " + status);
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}