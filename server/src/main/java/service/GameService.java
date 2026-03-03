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

import java.util.ArrayList;
import java.util.Collection;


import model.GameData;
import model.GameSum;
import model.AuthData;



public class GameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private int id = 0;

    public GameService(AuthDAO a, GameDAO g) {
        this.authDAO = a;
        this.gameDAO = g;
    }

    public ListResult listGames(ListRequest listRequest) throws DataAccessException {
        Collection<GameData> games = gameDAO.listGames();

        return new ListResult(games);
    }

    public CreateResult createGame(CreateRequest req) throws DataAccessException {
        id++;

        ChessGame chess = new ChessGame();

        GameData game = new GameData(id, null, null, req.gameName(), chess);

        gameDAO.createGame(game);
        
        CreateResult res = new CreateResult(id);

        return res;
    }

    public JoinResult joinGame(JoinRequest req, String authToken) throws DataAccessException {
        AuthData auth = authDAO.getAuth(authToken);

        if (auth == null) {
            throw new DataAccessException("unauthorized user");
        } 
        
        GameData game = gameDAO.getGame(req.gameID());

        if (game == null) {
            throw new DataAccessException("No Game");
        } else if (req.playerColor().equals("WHITE") && game.whiteUsername() != null) {
            throw new DataAccessException("Color has player");
        } else if (req.playerColor().equals("BLACK") && game.blackUsername() != null) {
            throw new DataAccessException("Color has player");
        }

        if (req.playerColor().equals("WHITE")) {
            GameData join = new GameData(req.gameID(), auth.username(), game.blackUsername(), game.gameName(), game.game());
            gameDAO.updateGame(join);
        } else if (req.playerColor().equals("BLACK")) {
            GameData join = new GameData(req.gameID(), game.whiteUsername(), auth.username(), game.gameName(), game.game());
            gameDAO.updateGame(join);
        }        
        JoinResult res = new JoinResult();

        return res;
    }


}
