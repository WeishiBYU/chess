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
import service.results.ClearResult;
import service.results.LoginResult;
import service.results.RegisterResult;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;

    }

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        if (userDAO.getUser(registerRequest.username()) != null) {
            throw new DataAccessException("username taken");
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

        RegisterResult res = new RegisterResult();
        
        return res;
    }

	public LoginResult login(LoginRequest loginRequest) throws DataAccessException{
        if (userDAO.getUser(loginRequest.username()) != null) {
            throw new DataAccessException("username taken");
        }
        
        AuthData authData = new AuthData(
            createToken(),
            loginRequest.username()
        );

        authDAO.createAuth(authData);

        RegisterResult res = new RegisterResult();
        
        return res;

    }

	public void logout(LogoutRequest logoutRequest) throws DataAccessException{}

    private String createToken() {
        return UUID.randomUUID().toString();
    }
}
