package dataaccess;

import com.google.gson.Gson;

import model.*;

import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

import java.util.ArrayList;
import java.util.Collection;

public class MySQLUserDAO extends BaseMySQLDAO implements UserDAO {

    public MySQLUserDAO() throws DataAccessException {
        configureDatabase(createStatements);
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        var statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        
        executeUpdate(statement, userData.username(), userData.password(), userData.email());
    }

    @Override
    public UserData getUser(String user) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM user WHERE username=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, user);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()), e);
        }
        return null;
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE user";
        executeUpdate(statement);
    }

    private UserData readUser(ResultSet rs) throws SQLException {
        var username = rs.getString("username");
        var password = rs.getString("password");
        var email = rs.getString("email");

        return new UserData(username, password, email);
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS user (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              PRIMARY KEY (`username`),
              INDEX(email)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };
}
