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
}
