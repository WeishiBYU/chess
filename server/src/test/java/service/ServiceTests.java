package service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.MySQLAuthDAO;
import dataaccess.MySQLGameDAO;
import dataaccess.MySQLUserDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import passoff.model.TestAuthResult;
import server.ResponseException;

import service.ClearService;
import service.GameService;
import service.UserService;
import service.requests.CreateRequest;
import service.requests.JoinRequest;
import service.requests.ListRequest;
import service.requests.LoginRequest;
import service.requests.LogoutRequest;
import service.requests.RegisterRequest;
import service.results.CreateResult;
import service.results.JoinResult;
import service.results.ListResult;
import service.results.LoginResult;
import service.results.LogoutResult;
import service.results.RegisterResult;


public class ServiceTests {
    private UserService userService;
    private ClearService clearService;
    private GameService gameService;
    
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;

    @BeforeEach
    public void setup() throws Exception {
        userDAO = new MySQLUserDAO();
        authDAO = new MySQLAuthDAO();
        gameDAO = new MySQLGameDAO();


        userService = new UserService(userDAO, authDAO);
        clearService = new ClearService(userDAO, authDAO, gameDAO);
        gameService = new GameService(authDAO, gameDAO);

        clearService.clear();
    }

    private RegisterResult createUser()  throws Exception{
        RegisterRequest req = new RegisterRequest("u", "p", "e");

        RegisterResult res = userService.register(req);

        return res;
    }

    private int createGame() throws Exception {
        CreateRequest req = new CreateRequest("1", "1");

        CreateResult res = gameService.createGame(req);

        return res.gameID();
    }


   @Test
    @Order(1)
    @DisplayName("clear1")
    public void clearSuccess() throws Exception {
        RegisterResult user = createUser();

        ListRequest ereq = new ListRequest(user.authToken());
        ListResult expected = gameService.listGames(ereq);

        createGame();

        clearService.clear();

        RegisterResult u = createUser();

        ListRequest lreq = new ListRequest(u.authToken());
        ListResult res = gameService.listGames(lreq);
        

        assertEquals(expected, res, "There should be no games after clear");
    }


    @Test
    @Order(3)
    @DisplayName("register1")
    public void registerSuccess() throws Exception {
        RegisterRequest req = new RegisterRequest("u", "p", "e");

        RegisterResult res = userService.register(req);
        

        assertEquals("u", res.username(), "username should be u");
    }


    @Test
    @Order(4)
    @DisplayName("register2")
    public void registerFailure() throws Exception {
        RegisterRequest req = new RegisterRequest(null, "p", "e");

        ResponseException ex = Assertions.assertThrows(
                ResponseException.class,
                () -> userService.register(req)
        );

        Assertions.assertEquals(400, ex.toHttpStatusCode());
        }


    @Test
    @Order(5)
    @DisplayName("login1")
    public void loginSuccess() throws Exception {
        RegisterResult reg = userService.register(new RegisterRequest("u", "p", "e"));

        LogoutRequest req = new LogoutRequest(reg.authToken());
        userService.logout(req);

        LoginResult log =  userService.login(new LoginRequest("u", "p"));

        Assertions.assertNotNull(log.authToken());
    }

    @Test
    @Order(6)
    @DisplayName("login2")
    public void loginFailureWrongPassword() throws Exception {
        userService.register(new RegisterRequest("u", "p", "e"));

        ResponseException ex = Assertions.assertThrows(
                ResponseException.class,
                () -> userService.login(new LoginRequest("u", "1"))
        );

        Assertions.assertEquals(401, ex.toHttpStatusCode());
    }

    @Test
    @Order(7)
    @DisplayName("logout1")
    public void logoutSuccess() throws Exception {
        RegisterResult reg = createUser();

        LogoutRequest req = new LogoutRequest(reg.authToken());
        LogoutResult res = userService.logout(req);

        LogoutResult expected = new LogoutResult();
        
        assertEquals(expected, res, "result so be empty");
}

    @Test
    @Order(8)
    @DisplayName("logout2")
    public void logoutFailure() throws Exception {

       createUser();

        LogoutRequest req = new LogoutRequest("wrong");

        ResponseException ex = Assertions.assertThrows(
                ResponseException.class,
                () -> userService.logout(req)
        );

        Assertions.assertEquals(401, ex.toHttpStatusCode());
    }

    @Test
    @Order(9)
    @DisplayName("listGames1")
    public void listGamesSuccess() throws Exception {
        createGame();
        RegisterResult reg = createUser();
        
        ListResult res = gameService.listGames(new ListRequest(reg.authToken()));
        
        Assertions.assertEquals(1, res.games().size());
    }

    @Test
    @Order(10)
    @DisplayName("listGames2")
    public void listGamesFailure() {
        
        ResponseException ex = Assertions.assertThrows(
                ResponseException.class,
                () -> gameService.listGames(new ListRequest("auth"))
        );

        Assertions.assertEquals(401, ex.toHttpStatusCode());
    }

    @Test
    @Order(11)
    @DisplayName("createGame1")
    public void createGameSuccess() throws Exception {
        CreateRequest req = new CreateRequest("1", "1");

        CreateResult res = gameService.createGame(req);

        assertEquals(gameDAO.getGame(1).gameID(), res.gameID(), "gameID should be 1");
    }

    @Test
    @Order(12)
    @DisplayName("createGame2")
    public void createGameFailure() throws Exception {
        CreateRequest req = new CreateRequest("1", null);

        ResponseException ex = Assertions.assertThrows(
                ResponseException.class,
                () -> gameService.createGame(req)
        );

        Assertions.assertEquals(400, ex.toHttpStatusCode());

    }

        @Test
    @Order(13)
    @DisplayName("joinGame1")
    public void joinGameSuccess() throws Exception {
        RegisterResult reg = createUser();
        int gameID = createGame();

        JoinRequest req = new JoinRequest(reg.authToken(), "WHITE", gameID);
        JoinResult res = gameService.joinGame(req, reg.authToken());

        Assertions.assertEquals(new JoinResult(), res);
    }

    @Test
    @Order(14)
    @DisplayName("joinGame2")
    public void joinGameFailure() throws Exception {
        RegisterResult reg = createUser();
        int gameID = createGame();

        JoinRequest req = new JoinRequest(reg.authToken(), "WHITE", gameID);
        
        ResponseException ex = Assertions.assertThrows(
                ResponseException.class,
                () -> gameService.joinGame(req, "bad-token")
        );

        Assertions.assertEquals(401, ex.toHttpStatusCode());

    }
}