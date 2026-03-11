package server;

import java.util.Map;
import com.google.gson.Gson;
import service.*;
import service.requests.*;
import service.results.*;
import dataaccess.*;
import io.javalin.Javalin;
import io.javalin.http.Context;


public class Server {
    private final UserService userService;
    private final ClearService clearService;
    private final GameService gameService;
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    private final Javalin javalin;

    public Server() {
        try {
            this.userDAO = new MySQLUserDAO();
            this.authDAO = new MySQLAuthDAO();
            this.gameDAO = new MySQLGameDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to initialize DAOs: " + e.getMessage(), e);
    }

        this.userService = new UserService(userDAO, authDAO);
        this.clearService = new ClearService(userDAO, authDAO, gameDAO);
        this.gameService = new GameService(authDAO, gameDAO);

        javalin = Javalin.create(config -> config.staticFiles.add("web"))

        .post("/user", this::register)
        .delete("/db", this::clear)
        .post("/session", this::login)
        .delete("/session", this::logout)
        .get("/game", this::listGames)
        .post("/game", this::createGame)
        .put("/game", this::joinGame)
        .exception(ResponseException.class, this::exceptionHandler);

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void exceptionHandler(ResponseException ex, Context ctx) {
        ctx.status(ex.toHttpStatusCode());
        ctx.result(ex.toJson());
    }

    private void clear(Context ctx) {
        try {
            clearService.clear();

            ctx.status(200);
            ctx.result(new Gson().toJson(Map.of()));
        } catch (DataAccessException e) {
            ctx.status(500);
            ctx.result(new Gson().toJson(Map.of("message", "Error: " + e.getMessage())));
        }

    }

    private void register(Context ctx) {
        try {
            RegisterRequest req = new Gson().fromJson(ctx.body(), RegisterRequest.class);
            RegisterResult res = userService.register(req);

            ctx.status(200);
            ctx.result(new Gson().toJson(res));
        } catch (DataAccessException e) {
            ctx.status(500);
            ctx.result(new Gson().toJson(Map.of("message", "Error: " + e.getMessage())));
        } catch (ResponseException e) {
            ctx.status(e.toHttpStatusCode());
            ctx.result(new Gson().toJson(Map.of("message", "Error: " + e.getMessage())));
        }
    }

    private void login(Context ctx) {
        try {
            LoginRequest req = new Gson().fromJson(ctx.body(), LoginRequest.class);
            LoginResult res = userService.login(req);

            ctx.status(200);
            ctx.result(new Gson().toJson(res));
        } catch (DataAccessException e) {
            ctx.status(500);
            ctx.result(new Gson().toJson(Map.of("message", "Error: " + e.getMessage())));
        } catch (ResponseException e) {
            ctx.status(e.toHttpStatusCode());
            ctx.result(new Gson().toJson(Map.of("message", "Error: " + e.getMessage())));
        } 
    }

    private void logout(Context ctx) {
        try {
            String auth = ctx.header("authorization");
            LogoutRequest req = new LogoutRequest(auth);
            LogoutResult res = userService.logout(req);

            ctx.status(200);
            ctx.result(new Gson().toJson(res));
        } catch (DataAccessException e) {
            ctx.status(500);
            ctx.result(new Gson().toJson(Map.of("message", "Error: " + e.getMessage())));
        } catch (ResponseException e) {
            ctx.status(e.toHttpStatusCode());
            ctx.result(new Gson().toJson(Map.of("message", "Error: " + e.getMessage())));
        } 
    }

    private void listGames(Context ctx) {
        try {
            String auth = ctx.header("authorization");
            ListRequest req = new ListRequest(auth);
            ListResult res = gameService.listGames(req);

            ctx.status(200);
            ctx.result(new Gson().toJson(res));
        } catch (DataAccessException e) {
            ctx.status(500);
            ctx.result(new Gson().toJson(Map.of("message", "Error: " + e.getMessage())));
        } catch (ResponseException e) {
            ctx.status(e.toHttpStatusCode());
            ctx.result(new Gson().toJson(Map.of("message", "Error: " + e.getMessage())));
        }
    }

    private void createGame(Context ctx) {
        try {
            String auth = ctx.header("authorization");

            if (authDAO.getAuth(auth) == null) {
                throw new ResponseException(401, "Error: unauthorized");
            }

            CreateRequest req = new Gson().fromJson(ctx.body(), CreateRequest.class);
            CreateResult res = gameService.createGame(req);

            ctx.status(200);
            ctx.result(new Gson().toJson(res));
        } catch (DataAccessException e) {
            ctx.status(500);
            ctx.result(new Gson().toJson(Map.of("message", "Error: " + e.getMessage())));
        } catch (ResponseException e) {
            ctx.status(e.toHttpStatusCode());
            ctx.result(new Gson().toJson(Map.of("message", "Error: " + e.getMessage())));
        }
    }

    private void joinGame(Context ctx) {
        try {
            String auth = ctx.header("authorization");

            JoinRequest req = new Gson().fromJson(ctx.body(), JoinRequest.class);
            JoinResult res = gameService.joinGame(req, auth);

            ctx.status(200);
            ctx.result(new Gson().toJson(res));
        } catch (DataAccessException e) {
            ctx.status(500);
            ctx.result(new Gson().toJson(Map.of("message", "Error: " + e.getMessage())));
        } catch (ResponseException e) {
            ctx.status(e.toHttpStatusCode());
            ctx.result(new Gson().toJson(Map.of("message", "Error: " + e.getMessage())));
        }
    }

}
