package service;

import java.util.UUID;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import service.requests.LoginRequest;
import service.requests.LogoutRequest;
import service.requests.RegisterRequest;
import service.results.LoginResult;
import service.results.RegisterResult;

public class UserService {
    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        if (userDAO.getUser(registerRequest.username()) != null) {
            
        }
    }

	public LoginResult login(LoginRequest loginRequest) throws DataAccessException{
        if  
        getUser(loginRequest.username());

    }

	public void logout(LogoutRequest logoutRequest) throws DataAccessException{}

    private String createToken() {
        return UUID.randomUUID().toString();
    }

}
