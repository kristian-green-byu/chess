import dataaccess.DataAccessException;
import server.ServerFacade;

import java.util.Arrays;

public class ChessClient {
    private final ServerFacade server;

    public ChessClient(String serverUrl) {
        this.server = new ServerFacade(serverUrl);
    }

    public String eval(String line){
        var result = "Invalid Input.";
        try {
            var tokens = line.toLowerCase().split(" ");
            if (tokens.length > 0){
                var cmd = tokens[0];
                var params = Arrays.copyOfRange(tokens, 1, tokens.length);
                result = switch (cmd) {
                    case "register" -> register(params);
                    case "login" -> login(params);
                    case "logout" -> logout();
                    case "listGames" -> listGames();
                    case "createGame" -> createGame(params);
                    case "joinGame" -> joinGame(params);
                    case "clear" -> clear();
                    case "quit" -> "quit";
                    default -> help();
                };
            }
        } catch (DataAccessException e) {
            result = e.getMessage();
        }
        return result;
    }

    public String register(String... params) throws DataAccessException {
        return params[0];
    }

    public String login(String... params) throws DataAccessException {
        return params[0];
    }

    public String logout() throws DataAccessException {
        return "placeholder";
    }

    public String listGames() throws DataAccessException {
        return "placeholder";
    }

    public String createGame(String... params) throws DataAccessException {
        return "placeholder";
    }

    public String joinGame(String... params) throws DataAccessException {
        return "placeholder";
    }

    public String clear() throws DataAccessException {
        return "placeholder";
    }

    public String help() {
        return """
                - register <username> <password> <email>
                - login <username> <password>
                - logout
                - listGames
                - createGame <gameName>
                - joinGame <WHITE|BLACK> <gameID>
                - clear
                - quit
                """;
    }
}
