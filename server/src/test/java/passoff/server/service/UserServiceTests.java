package service;

import dataaccess.*;
import service.requests.RegisterRequest;
import org.junit.jupiter.api.*;
import passoff.model.TestUser;
import service.UserService;
import service.ClearService;


public class UserServiceTests {
    // These are the actual objects we are testing
    private UserService userService;
    private ClearService clearService;
    
    // We need the DAOs to pass into the services
    private UserDAO userDAO;
    private AuthDAO authDAO;

    @BeforeEach
    public void setup() {
        // 1. Initialize your Memory DAOs
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();

        // 2. Initialize your Services with those DAOs
        userService = new UserService(userDAO, authDAO);
        
        // 3. (Optional) If you want to use your ClearService to wipe data
        // instead of just making new DAOs, initialize it here too.
        clearService = new ClearService(userDAO, authDAO, new MemoryGameDAO());
        clearService.clear(); 
    }

    @Test
    @DisplayName("Register Success")
    public void registerSuccess() throws Exception {
        var request = new RegisterRequest("player1", "password", "p1@test.com");
        var result = userService.register(request);
        
        Assertions.assertNotNull(result.authToken(), "AuthToken should not be null on success");
        Assertions.assertEquals("player1", result.username());
    }

    @Test
    @DisplayName("Register Failure - Duplicate User")
    public void registerFail() throws Exception {
        var request = new RegisterRequest("player1", "password", "p1@test.com");
        
        // First one works
        userService.register(request);
        
        // Second one should throw our custom ResponseException
        Assertions.assertThrows(dataaccess.ResponseException.class, () -> {
            userService.register(request);
        }, "Registering the same user twice should throw an exception");
    }
}