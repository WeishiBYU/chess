package dataaccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public abstract class BaseMySQLDAO {

        protected int executeUpdate(String statement, Object... params) throws DataAccessException {
    try (Connection conn = DatabaseManager.getConnection();
         PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {

        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            if (param instanceof Integer p) {
                ps.setInt(i + 1, p);
            } else if (param == null) {
                ps.setNull(i + 1, NULL);
            } else {
                ps.setString(i + 1, param.toString());
            }
        }

        ps.executeUpdate();

        try (ResultSet rs = ps.getGeneratedKeys()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    } catch (SQLException e) {
        throw new DataAccessException(
                String.format("unable to update database: %s, %s", statement, e.getMessage()), e);
    }
}

protected void configureDatabase(String[] createStatements) throws DataAccessException {
    DatabaseManager.createDatabase();
    try (Connection conn = DatabaseManager.getConnection()) {
        for (String statement : createStatements) {
            try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        }
    } catch (SQLException ex) {
        throw new DataAccessException(
                String.format("Unable to configure database: %s", ex.getMessage()), ex);
        }
    }
}


