package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import dataaccess.GameDAO;
import requests.ClearRequest;
import responses.ClearResponse;

public class ClearService {
    private final AuthDAO authDAO;
    private final UserDAO userDAO;
    private final GameDAO gameDAO;

    public ClearService(AuthDAO authDAO, UserDAO userDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
    }

    public ClearResponse clear(ClearRequest clearRequest) {
        authDAO.clearAuthData();
        userDAO.clearUserData();
        gameDAO.clearGameData();
        return new ClearResponse();
    }
}
