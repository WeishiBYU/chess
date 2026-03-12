package dataaccess;

import com.google.gson.Gson;

import chess.ChessGame;
import model.*;

import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

import java.util.ArrayList;
import java.util.Collection;

public class MySQLGameDAO extends BaseMySQLDAO implements GameDAO {

    public MySQLGameDAO() throws DataAccessException {
        configureDatabase(createStatements);
    }

    @Override
    public int createGame(GameData gameData) throws DataAccessException {

        String game = new Gson().toJson(gameData.game());

        var statement = "INSERT INTO game (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        return executeUpdate(statement, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
    }

    @Override
    public GameData getGame(int id) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT id, whiteUsername, blackUsername, gameName, game FROM game WHERE id=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()), e);
        }
        return null;
    }
    
    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        Collection<GameData> result = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT id, whiteUsername, blackUsername, gameName, game FROM game";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readGame(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()), e);
        }
        return result;
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {

        String game = new Gson().toJson(gameData.game());

        var statement = "UPDATE game SET whiteUsername=?, blackUsername=?, gameName=?, game=? WHERE id=?";
        executeUpdate(statement, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game, gameData.gameID());
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE game";
        executeUpdate(statement);
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        var id = rs.getInt("id");
        var whiteUsername = rs.getString("whiteUsername");
        var blackUsername = rs.getString("blackUsername");
        var gameName = rs.getString("gameName");
        var json = rs.getString("game");
        ChessGame game = new Gson().fromJson(json, ChessGame.class);

        return new GameData(id, whiteUsername, blackUsername, gameName, game);
    }


    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS game (
              `id` int NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(256) DEFAULT NULL,
              `blackUsername` varchar(256) DEFAULT NULL,
              `gameName` varchar(256) NOT NULL,
              `game` TEXT NOT NULL,
              PRIMARY KEY (`id`),
              INDEX(gameName)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };


}
