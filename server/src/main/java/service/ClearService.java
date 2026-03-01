package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import service.results.ClearResult;

public class ClearService {

    private final AuthDAO a;
    private final GameDAO g;
    private final UserDAO u;

    public ClearService(UserDAO u,AuthDAO a, GameDAO g) {
        this.u = u;
        this.a = a;
        this.g = g;
    }

    public ClearResult clear() throws DataAccessException {
            a.clear();
            g.clear();
            u.clear();

            ClearResult res = new ClearResult();

            return res;
    }
}
