package service;


import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import requests.ListGamesRequest;
import responses.CreateGameResponse;
import responses.JoinGameResponse;
import responses.ListGamesResponse;

import java.util.Collection;

public class GameService {

    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public CreateGameResponse createGame(CreateGameRequest createGameRequest) throws DataAccessException {
        if (createGameRequest.gameName() == null) {
            throw new DataAccessException("unauthorized");
        } else if (authDAO.getAuthData(createGameRequest.authToken()) == null) {
            throw new DataAccessException("unauthorized");
        }
        AuthData authData = authDAO.getAuthData(createGameRequest.authToken());
        if (authData == null) {
            throw new DataAccessException("unauthorized");
        }
        int gameID = gameDAO.createGame(createGameRequest.gameName());
        return new CreateGameResponse(gameID);
    }

    public JoinGameResponse joinGame(JoinGameRequest joinGameRequest) throws DataAccessException {
        AuthData authData = authDAO.getAuthData(joinGameRequest.authToken());
        if (authData == null) {
            throw new DataAccessException("unauthorized");
        }
        GameData gameData = gameDAO.getGame(joinGameRequest.gameID());
        if (gameData == null) {
            throw new DataAccessException("bad request");
        }
        if (joinGameRequest.playerColor() == null) {
            throw new DataAccessException("bad request");
        } else if (joinGameRequest.playerColor() == ChessGame.TeamColor.WHITE) {
            if (gameData.whiteUsername() != null) {
                throw new DataAccessException("already taken");
            }

        } else {
            if (gameData.blackUsername() != null) {
                throw new DataAccessException("already taken");
            }
        }
        gameDAO.updateGame(authData.username(), joinGameRequest.playerColor(), gameData);
        return new JoinGameResponse();
    }

    public ListGamesResponse listGames(ListGamesRequest listGamesRequest) throws DataAccessException {
        AuthData authData = authDAO.getAuthData(listGamesRequest.authToken());
        if (authData == null) {
            throw new DataAccessException("unauthorized");
        }
        Collection<GameData> games = gameDAO.listGames();
        return new ListGamesResponse(games);
    }
}
