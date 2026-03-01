package dataaccess;

import model.UserData;
import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    private final HashMap<String, UserData> users = new HashMap<>();

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        users.put(userData.username(), userData);
    }

    @Override
    public UserData getUser(String user) throws DataAccessException {
        UserData userData = users.get(user);

        return userData;
    }

    @Override
    public void clear() throws DataAccessException {
        users.clear();
    }
}
