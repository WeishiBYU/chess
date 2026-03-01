package service;

import java.util.UUID;

import dataaccess.DataAccessException;
import service.requests.LoginRequest;
import service.requests.LogoutRequest;
import service.requests.RegisterRequest;
import service.results.LoginResult;
import service.results.RegisterResult;

public class UserService {
    public RegisterResult register(RegisterRequest registerRequest) {

        
    }

	public LoginResult login(LoginRequest loginRequest) {
        getUser(loginRequest.username());

    }

	public void logout(LogoutRequest logoutRequest) throws DataAccessException{}

    private String createToken() {
        return UUID.randomUUID().toString();
    }


}
