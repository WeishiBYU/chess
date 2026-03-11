package dataaccess;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import chess.ChessGame;

import java.util.ArrayList;
import java.util.Collection;


import model.*;

public class DataAccessTests {
    
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;

    @BeforeEach
    public void setup() throws Exception {
        userDAO = new MySQLUserDAO();
        authDAO = new MySQLAuthDAO();
        gameDAO = new MySQLGameDAO();

        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
    }

    private UserData createTestUser() {
        String hashedPassword = BCrypt.hashpw("password", BCrypt.gensalt());

        return new UserData("username", hashedPassword, "email");
    }

    private GameData createTestGame() {
        ChessGame chess = new ChessGame();

        return new GameData(0, null, null, "test", chess);
        

    }

    private AuthData createTestAuth() {
        String authToken = UUID.randomUUID().toString();
        return new AuthData(authToken, "username");
    }


   @Test
    @Order(1)
    @DisplayName("clear game")
    public void clearGame() throws Exception {

        Collection<GameData> expected = new ArrayList<>();

        gameDAO.createGame(createTestGame());

        gameDAO.clear();

        Collection<GameData> res = gameDAO.listGames();

        assertEquals(expected, res, "There should be no games after clear");
    }

    @Test
    @Order(2)
    @DisplayName("clear user")
    public void clearUser() throws Exception {        
        userDAO.createUser(createTestUser());

        userDAO.clear();

        UserData res = userDAO.getUser("username");
        
        assertNull(res, "There should be no user after clear");
    }

    @Test
    @Order(3)
    @DisplayName("clear auth")
    public void clearAuth() throws Exception {
        AuthData auth = createTestAuth();

        authDAO.createAuth(auth);

        authDAO.clear();

        AuthData res = authDAO.getAuth(auth.authToken());

        assertNull(res, "There should be no auth after clear");
    }


    @Test
    @Order(4)
    @DisplayName("createUser1")
    public void createUserSuccess() throws Exception {
        userDAO.createUser(createTestUser());


        UserData res = userDAO.getUser("username");
        

        assertEquals("email", res.email(), "email should be email");
    }


    @Test
    @Order(5)
    @DisplayName("createUser2")
    public void createUserFailure() throws Exception {     
        UserData u1 = createTestUser();
        userDAO.createUser(u1);

        DataAccessException ex = Assertions.assertThrows(
                DataAccessException .class,
                () -> userDAO.createUser(u1)

        );

        Assertions.assertNotNull(ex.getMessage());
    }


    @Test
    @Order(6)
    @DisplayName("GetUser1")
    public void getUserSuccess() throws Exception {
        userDAO.createUser(createTestUser());

        UserData user = userDAO.getUser("username");

        Assertions.assertNotNull(user);
    }

    @Test
    @Order(7)
    @DisplayName("GetUser2")
    public void getUserFailure() throws Exception {
        UserData user = userDAO.getUser("username");

        Assertions.assertNull(user);
    }

    @Test
    @Order(8)
    @DisplayName("createGame1")
    public void createGameSuccess() throws Exception {
        gameDAO.createGame(createTestGame());


        GameData res = gameDAO.getGame(1);
        

        assertNotNull(res);
    }


    @Test
    @Order(9)
    @DisplayName("createGame2")
    public void createGameFailure() throws Exception {     
        GameData bad = new GameData(0, null, null, null, new ChessGame());

        DataAccessException ex = Assertions.assertThrows(
                DataAccessException .class,
                () -> gameDAO.createGame(bad)

        );

        Assertions.assertNotNull(ex.getMessage());
    }


    @Test
    @Order(10)
    @DisplayName("GetGame1")
    public void getGameSuccess() throws Exception {
        gameDAO.createGame(createTestGame());

        GameData game = gameDAO.getGame(1);

        Assertions.assertNotNull(game);
    }

    @Test
    @Order(11)
    @DisplayName("GetGame2")
    public void getGameFailure() throws Exception {
        GameData game = gameDAO.getGame(2);

        Assertions.assertNull(game);
    }

    @Test
    @Order(12)
    @DisplayName("GetGames1")
    public void getGameListSuccess() throws Exception {
        gameDAO.createGame(createTestGame());

        Collection<GameData> games = gameDAO.listGames();

        Assertions.assertEquals(1, games.size());
    }

    @Test
    @Order(13)
    @DisplayName("GetGames2")
    public void getGameListEmpty() throws Exception {

        Collection<GameData> games = gameDAO.listGames();

        Assertions.assertEquals(0, games.size());
    }


    @Test
    @Order(14)
    @DisplayName("Update Game1")
    public void updateGameSuccess() throws Exception {
        gameDAO.createGame(createTestGame());

        GameData gamenew = new GameData(1, "cheese", null, "reed", null);
     
        gameDAO.updateGame(gamenew);

        GameData game = gameDAO.getGame(1);

        Assertions.assertEquals("cheese", game.whiteUsername());
    }

    @Test
    @Order(15)
    @DisplayName("Update Game2")
    public void updateGameFailure() throws Exception {
        gameDAO.createGame(createTestGame());

        GameData gamenew = new GameData(1, null, null, null, null);
     
        
        DataAccessException ex = Assertions.assertThrows(
                DataAccessException .class,
                () -> gameDAO.updateGame(gamenew)

        );

        Assertions.assertNotNull(ex);
    }

    @Test
    @Order(16)
    @DisplayName("createAuth1")
    public void createAuthSuccess() throws Exception {
        AuthData auth = createTestAuth();

        authDAO.createAuth(auth);


        AuthData res = authDAO.getAuth(auth.authToken());
        

        assertNotNull(res);
    }


    @Test
    @Order(17)
    @DisplayName("createAuth2")
    public void createAuthFailure() throws Exception {     
        AuthData bad = new AuthData(null, null);

        DataAccessException ex = Assertions.assertThrows(
                DataAccessException .class,
                () -> authDAO.createAuth(bad)

        );

        Assertions.assertNotNull(ex.getMessage());
    }


    @Test
    @Order(18)
    @DisplayName("GetAuth1")
    public void getAuthSuccess() throws Exception {
        AuthData auth = createTestAuth();

        authDAO.createAuth(auth);

        AuthData authData = authDAO.getAuth(auth.authToken());

        Assertions.assertEquals(authData.username(), auth.username());
    }

    @Test
    @Order(19)
    @DisplayName("GetAuth2")
    public void getAuthFailure() throws Exception {
        AuthData auth = authDAO.getAuth("2");

        Assertions.assertNull(auth);
    }

    @Test
    @Order(20)
    @DisplayName("deleteAuth1")
    public void deleteAuthSuccess() throws Exception {
        AuthData auth = createTestAuth();

        authDAO.createAuth(auth);

        authDAO.deleteAuth(auth.authToken());

        AuthData res = authDAO.getAuth(auth.authToken());
        

        assertNull(res);
    }


    @Test
    @Order(21)
    @DisplayName("deleteAuth2")
    public void deleteAuthFailure() throws Exception {  
        AuthData auth = createTestAuth();

        authDAO.createAuth(auth);

        AuthData res = authDAO.getAuth(auth.authToken());

        authDAO.deleteAuth(null);

        Assertions.assertNotNull(res);
    }
}