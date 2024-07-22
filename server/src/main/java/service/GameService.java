package service;


import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import requests.CreateGameRequest;
import responses.CreateGameResponse;

public class GameService {

    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public CreateGameResponse createGame(CreateGameRequest createGameRequest) throws DataAccessException {
        if(createGameRequest.gameName()==null){
            throw new DataAccessException("unauthorized");
        }
        else if(authDAO.getAuthData(createGameRequest.authToken())==null){
            throw new DataAccessException("unauthorized");
        }
        AuthData authData = authDAO.getAuthData(createGameRequest.authToken());
        if(authData==null){
            throw new DataAccessException("unauthorized");
        }
        int gameID = gameDAO.createGame(createGameRequest.gameName());
        return new CreateGameResponse(gameID);
    }

    public void clearGames(){
    }
}
