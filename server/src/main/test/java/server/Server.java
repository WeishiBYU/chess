package server;

import io.javalin.*;
import service.LoginService;
import service.JoinGameService;
import service.RegisterService;


public class Server {
    private final ChessService service;
    private final Javalin javalin;

    public Server(ChessService service) {
        
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        .post("/user", this::register);
        .delete("/db", this::);
        .post("/session", this::);
        .get("/game", this::);
        .post("/game", this::);
        .put("/game", this::);
        .exception(ResponseException.class, this::exceptionHandler)

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

    private void clear(Context ctx) throws ResponseException {

    }

    private void register(Context ctx) throws ResponseException {
        
    }

    private void login(Context ctx) throws ResponseException {

    }

    private void logout(Context ctx) throws ResponseException {

    }

    private void listGames(Context ctx) throws ResponseException {

    }

    private void creatGame(Context ctx) throws ResponseException {

    }

    private void joinGame(Context ctx) throws ResponseException {

    }
}
