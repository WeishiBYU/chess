package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import service.results.ClearResult;

public class GameService {
    private final AuthDAO a;
    private final GameDAO g;

    public GameService(AuthDAO a, GameDAO g) {
        this.a = a;
        this.g = g;
    }

    public ClearResult clear() throws DataAccessException {
            a.clear();
            g.clear();

            ClearResult res = new ClearResult();

            return res;
    }
}
