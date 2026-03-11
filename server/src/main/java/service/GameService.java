package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import service.requests.CreateRequest;
import service.requests.JoinRequest;
import service.requests.ListRequest;
import service.results.CreateResult;
import service.results.JoinResult;
import service.results.ListResult;
import chess.ChessGame;

import java.util.Collection;


import model.GameData;
import server.ResponseException;
import model.AuthData;



public class GameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    
    public GameService(AuthDAO a, GameDAO g) {
        this.authDAO = a;
        this.gameDAO = g;
    }

    public ListResult listGames(ListRequest listRequest) throws DataAccessException, ResponseException {
        if (authDAO.getAuth(listRequest.authToken()) == null) {
            throw new ResponseException(401, "unauthorized");
        }
        
        Collection<GameData> games = gameDAO.listGames();

        return new ListResult(games);
    }

    public CreateResult createGame(CreateRequest req) throws DataAccessException, ResponseException {

        if (req.gameName() == null) {
            throw new ResponseException(400, "Error: bad request");
        }

        ChessGame chess = new ChessGame();

        GameData game = new GameData(0, null, null, req.gameName(), chess);
        
        CreateResult res = new CreateResult(gameDAO.createGame(game));

        return res;
    }

    public JoinResult joinGame(JoinRequest req, String authToken) throws DataAccessException, ResponseException {
        AuthData auth = authDAO.getAuth(authToken);

        if (auth == null) {
            throw new ResponseException(401, "unauthorized user");
        } 
        
        GameData game = gameDAO.getGame(req.gameID());

        if (game == null || req.playerColor() == null) {
            throw new ResponseException(400, "bad request");
        } else if (req.playerColor().equals("WHITE") && game.whiteUsername() != null) {
            throw new ResponseException(403, "Color has player");
        } else if (req.playerColor().equals("BLACK") && game.blackUsername() != null) {
            throw new ResponseException(403, "Color has player");
        }

        if (req.playerColor().equals("WHITE")) {
            GameData join = new GameData(req.gameID(), auth.username(), game.blackUsername(), game.gameName(), game.game());
            gameDAO.updateGame(join);
        } else if (req.playerColor().equals("BLACK")) {
            GameData join = new GameData(req.gameID(), game.whiteUsername(), auth.username(), game.gameName(), game.game());
            gameDAO.updateGame(join);
        } else {
                throw new ResponseException(400, "bad request");
        }


        JoinResult res = new JoinResult();

        return res;
    }


}
