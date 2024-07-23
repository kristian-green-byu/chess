package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

public class ClearService {
    private final AuthDAO authDAO;
    private final UserDAO userDAO;
    private final GameDAO gameDAO;

    public ClearService(AuthDAO authDAO, UserDAO userDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
    }

    public void clear() {
        authDAO.clearAuthData();
        userDAO.clearUserData();
        gameDAO.clearGameData();
    }
}
