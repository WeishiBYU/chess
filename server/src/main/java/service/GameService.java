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
        
        ListResult res = new ListResult(games);

        return res;
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
        AuthData auth = authDAO.getAuth(req.authToken());

        if (auth == null) {
            throw new DataAccessException("Error: unauthorized");
        } 
        
        GameData game = gameDAO.getGame(req.gameID());

        if (game == null) {
            throw new DataAccessException("No Game");
        } else if (req.playerColor() == "WHITE" && game.whiteUsername() != null) {
            throw new DataAccessException("Color has player");
        } else if (req.playerColor() == "BLACK" && game.blackUsername() != null) {
            throw new DataAccessException("Color has player");
        }

        if (req.playerColor() == "WHITE") {
            GameData join = new GameData(req.gameID(), auth.username(), game.blackUsername(), game.gameName(), game.game());
            gameDAO.updateGame(join);
        } else if (req.playerColor() == "BLACK") {
            GameData join = new GameData(req.gameID(), game.whiteUsername(), auth.username(), game.gameName(), game.game());
            gameDAO.updateGame(join);
        }        
        JoinResult res = new JoinResult();

        return res;
    }


}
