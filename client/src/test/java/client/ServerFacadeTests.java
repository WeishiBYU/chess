package client;
import exception.ResponseException;

import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;


public class ServerFacadeTests {
    private static ServerFacade facade;
    private static Server server;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @BeforeEach
    static void clear() throws ResponseException {
        facade.clearDB();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    public void registerSuccess() throws Exception {
        var authData = facade.register("Player1", "password123", "p1@email.com");
        Assertions.assertNotNull(authData.authToken());
        Assertions.assertEquals("Player1", authData.username());
    }

    @Test
    public void registerDuplicateUser() throws Exception {
        facade.register("ExistingUser", "password", "e@mail.com");

        Assertions.assertThrows(ResponseException.class, () -> {
            facade.register("ExistingUser", "newpassword", "new@mail.com");
        });
    } 

    @Test
    public void logoutSuccess() throws Exception {
        var authData = facade.register("Player1", "password123", "p1@email.com");
        
        Assertions.assertDoesNotThrow( () -> {
            facade.logout(authData.authToken());
        });    
    }

    @Test
    public void logoutNoToken() throws Exception {
        var authData = facade.register("Player1", "password123", "p1@email.com");
        
        facade.logout(authData.authToken());

        Assertions.assertThrows(ResponseException.class, () -> {
            facade.logout(null);
        });
    } 

    @Test
    public void loginSuccess() throws Exception {
        var authData = facade.register("Player1", "password123", "p1@email.com");
        facade.logout(authData.authToken());
        var auth = facade.login("Player1", "password123");

        Assertions.assertNotNull(auth.authToken());
        Assertions.assertEquals("Player1", auth.username());
    }

    @Test
    public void loginWrongPassword() throws Exception {
        var authData = facade.register("Player1", "password123", "p1@email.com");
        facade.logout(authData.authToken());


        Assertions.assertThrows(ResponseException.class, () -> {
            facade.login("Player1", "pass");        
        });
    } 

    @Test
    public void CreateSuccess() throws Exception {
        var authData = facade.register("Player1", "password123", "p1@email.com");
       
        var game = facade.createGame(authData.authToken(), "game1");

        Assertions.assertNotNull(game);
    }

    @Test
    public void CreateNotLoggedIn() throws Exception {
        Assertions.assertThrows(ResponseException.class, () -> {
            facade.createGame(null, "game1");
        });
    } 

    @Test
    public void listSuccess() throws Exception {
        var authData = facade.register("Player1", "password123", "p1@email.com");
       
        facade.createGame(authData.authToken(), "game1");

        var games = facade.listGames(authData.authToken());


        Assertions.assertNotNull(games);
    }

    @Test
    public void listNoAuth() throws Exception {
        Assertions.assertThrows(ResponseException.class, () -> {
            facade.listGames(null);
        });
    } 

    @Test
    public void joinSuccess() throws Exception {
        var authData = facade.register("Player1", "password123", "p1@email.com");
        facade.createGame(authData.authToken(), "game1");

        var game = facade.joinGame(authData.authToken(), "WHITE", 1);        


        Assertions.assertNotNull(game);
    }

    @Test
    public void JoinGameNoLogin() throws Exception {
        var authData = facade.register("Player1", "password123", "p1@email.com");
       
        facade.createGame(authData.authToken(), "game1");

        facade.logout(authData.authToken());

        Assertions.assertThrows(ResponseException.class, () -> {
            facade.joinGame(null, "WHITE", 1);        
        });
    } 

        @Test
    public void observeSuccess() throws Exception {
        var authData = facade.register("Player1", "password123", "p1@email.com");
        facade.createGame(authData.authToken(), "game1");

        Assertions.assertDoesNotThrow( () -> {
            facade.observeGame(authData.authToken(), 1);
        });   
    }

    @Test
    public void ObserveGameNoLogin() throws Exception {
        var authData = facade.register("Player1", "password123", "p1@email.com");
       
        facade.createGame(authData.authToken(), "game1");

        facade.logout(authData.authToken());

        Assertions.assertThrows(ResponseException.class, () -> {
            facade.observeGame(null, 1);        
        });
    } 

}
