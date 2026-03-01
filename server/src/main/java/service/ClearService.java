package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

public class ClearService {

    public void clear() {
        AuthDAO.clear();
        GameDAO.clear();
        UserDAO.clear();
    }
}
