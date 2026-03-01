package dataaccess;

import model.GameData;
import java.util.HashMap;

public interface GameDAO {

    public void createGame(GameData gameData) throws DataAccessException;

    public GameData getGame(int gameID) throws DataAccessException;

    public HashMap<Integer, GameData> listGames() throws DataAccessException;

    public void updateGame(GameData gameData) throws DataAccessException;

    public void clear() throws DataAccessException;

    
}
