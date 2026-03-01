package server;

import io.javalin.*;
import io.javalin.http.Context;
import service.LoginService;
import service.JoinGameService;
import service.RegisterService;


public class Server {
    private final ChessService service;
    private final Javalin javalin;

    public Server(ChessService service) {
        
        javalin = Javalin.create(config -> config.staticFiles.add("web"))

        // Register your endpoints and exception handlers here.
        .post("/user", this::register)
        .delete("/db", this::login)
        .post("/session", this::logout)
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
            service.clearDB();
            
            ctx.status(200);
            ctx.json(Map.of());
        } catch (DataAccessException e) {
            ctx.status(500);
            ctx.json(Map.of("message", "Error: " + e.getMessage()));
        }

    }

    private void register(Context ctx) {
        try {
            LoginRequest req = new Gson().fromJson(ctx.body(), RegisterRequest.class);
            RegisterResult res = service.RegisterResult(req);

            ctx.status(200);
            ctx.json(res);
        } catch (DataAccessException e) {
            if (e.getMessage().contains("already taken")) {
                ctx.status(403);
            } else {
                ctx.status(400);
            }
            ctx.json(Map.of("message", e.getMessage()));
        }
    }

    private void login(Context ctx) {

    }

    private void logout(Context ctx) {

    }

    private void listGames(Context ctx) {

    }

    private void createGame(Context ctx) {

    }

    private void joinGame(Context ctx) {

    }
}
