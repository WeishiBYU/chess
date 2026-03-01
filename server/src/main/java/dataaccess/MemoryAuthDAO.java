package dataaccess;

import java.util.HashMap;

import model.AuthData;

public class MemoryAuthDAO implements AuthDAO{
private final HashMap<String, AuthData> auths = new HashMap<>();

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        auths.put(auth.authToken(), auth);
    }


    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        AuthData auth = auths.get(authToken);

        return auth;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        auths.remove(authToken);
    }

    @Override
    public void clear() throws DataAccessException {
        auths.clear();
    }
}
