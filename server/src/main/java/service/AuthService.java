package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import service.results.ClearResult;

public class AuthService {
    private final AuthDAO a;

    public AuthService(AuthDAO a) {
        this.a = a;
    }

    public ListResult listGames() throws DataAccessException {
            a.clear();

            ClearResult res = new ClearResult();

            return res;
    }
}
