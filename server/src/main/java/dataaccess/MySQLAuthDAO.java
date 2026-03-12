package dataaccess;

import model.*;

import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySQLAuthDAO extends BaseMySQLDAO implements AuthDAO {

    public MySQLAuthDAO()  throws DataAccessException {
        configureDatabase(createStatements);
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        var statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        executeUpdate(statement, authData.authToken(), authData.username());
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, authToken FROM auth WHERE authToken=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readAuth(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()), e);
        }
        return null;
    }
    
    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        var statement = "DELETE FROM auth WHERE authToken=?";
        executeUpdate(statement, authToken);
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE auth";
        executeUpdate(statement);
    }

    private AuthData readAuth(ResultSet rs) throws SQLException {
        var username = rs.getString("username");
        var authToken = rs.getString("authToken");

        return new AuthData(authToken, username);
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS auth (
              `username` varchar(256) NOT NULL,
              `authToken` varchar(256) NOT NULL,
              PRIMARY KEY (`authToken`),
              INDEX(username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };
}
