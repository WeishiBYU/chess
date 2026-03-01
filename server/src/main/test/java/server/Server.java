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
        .delete("/db", this::login);
        .post("/session", this::logout);
        .get("/game", this::listGames);
        .post("/game", this::createGame);
        .put("/game", this::joinGame);
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

    private void clear(Context ctx) throws ResponseException {
        service.clearDB();
        ctx.status(204);
    }

    private void register(Context ctx) throws ResponseException {
        UserData user = new Gson().fromJson(ctx.body(), UserData.class);
        user = service. 
    }

    private void login(Context ctx) throws ResponseException {

    }

    private void logout(Context ctx) throws ResponseException {

    }

    private void listGames(Context ctx) throws ResponseException {

    }

    private void createGame(Context ctx) throws ResponseException {

    }

    private void joinGame(Context ctx) throws ResponseException {

    }
}
