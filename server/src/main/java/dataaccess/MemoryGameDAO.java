package dataaccess;

import java.util.HashMap;

import model.GameData;

public class MemoryGameDAO implements GameDAO {
    private final HashMap<Integer, GameData> games = new HashMap<>();

    @Override
    public void createGame(GameData gameData) throws DataAccessException {
        games.put(gameData.gameID(), gameData);
    }


    @Override
    public GameData getGame(int gameID) throws DataAccessException{
        GameData game = games.get(gameID);

        return game;
    }

    @Override
    public HashMap<Integer, GameData> listGames() throws DataAccessException {
        return games;
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        games.put(gameData.gameID(), gameData);
    }

    @Override
    public void clear() throws DataAccessException {
        games.clear();
    }
}
