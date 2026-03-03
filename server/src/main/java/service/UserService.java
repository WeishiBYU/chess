package service;

import java.util.UUID;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import server.ResponseException;
import service.requests.LoginRequest;
import service.requests.LogoutRequest;
import service.requests.RegisterRequest;
import service.results.LoginResult;
import service.results.LogoutResult;
import service.results.RegisterResult;
import service.requests.UserRequest;
import service.results.UserResult;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;

    }

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException, ResponseException {
        if (userDAO.getUser(registerRequest.username()) != null) {
            throw new ResponseException(403, "username taken");
        }

        if (registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null) {
           throw new ResponseException(400, "bad request"); 
        }

        UserData userData = new UserData(
            registerRequest.username(),
            registerRequest.password(), 
            registerRequest.email()
        );
        
        userDAO.createUser(userData);

        AuthData authData = new AuthData(
            createToken(),
            registerRequest.username()
        );

        authDAO.createAuth(authData);

        RegisterResult res = new RegisterResult(authData.username(), authData.authToken());
        
        return res;
    }

	public LoginResult login(LoginRequest loginRequest) throws DataAccessException, ResponseException{
        UserData user = userDAO.getUser(loginRequest.username());

        if (loginRequest.username() == null || loginRequest.password() == null) {
           throw new ResponseException(400, "bad request"); 
        }

        if (user == null || !user.password().equals(loginRequest.password())) {
            throw new ResponseException(401, "Error: unauthorized");
        }
        
        AuthData authData = new AuthData(
            createToken(),
            loginRequest.username()
        );

        authDAO.createAuth(authData);

        LoginResult res = new LoginResult(authData.username(), authData.authToken());
        
        return res;

    }


	public LogoutResult logout(LogoutRequest logoutRequest) throws DataAccessException, ResponseException {
        if (authDAO.getAuth(logoutRequest.authToken()) == null) {
            throw new ResponseException(401, "Error: unauthorized");
        }
        
        authDAO.deleteAuth(logoutRequest.authToken());
        
        LogoutResult res = new LogoutResult();

        return res;
    }

    private String createToken() {
        return UUID.randomUUID().toString();
    }
}
