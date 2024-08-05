import chess.ChessGame;
import com.google.gson.Gson;
import responses.LoginResponse;
import responses.RegisterResponse;
import serverfacade.ServerFacade;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class ChessClient {
    private static ServerFacade server;
    private static String authToken;
    private boolean postLogin;

    public ChessClient(int port) {
        server = new ServerFacade("http://localhost:"+port);
        postLogin = false;
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
                    case "logout" -> logout(authToken);
                    case "list" -> listGames(authToken);
                    case "create" -> createGame(params);
                    case "joinGame" -> joinGame(params);
                    case "clear" -> clear();
                    case "quit" -> "quit";
                    default -> help();
                };
            }
        } catch (IOException e) {
            result = e.getMessage();
        }
        return result;
    }

    public String register(String... params) throws IOException {
        try{
            if(params.length == 3){
                String username = params[0];
                String password = params[1];
                String email = params[2];
                RegisterResponse registerResponse = server.register(username, password, email);
                authToken = registerResponse.authToken();
                postLogin = true;
                return "Registration successful. You are now logged in as "+username;
            }
        } catch (Exception ignore){
        }
        throw new IOException("Expected: <username> <password> <email>");
    }

    public String login(String... params) throws IOException {
        try{
            if(params.length == 2){
                var username = params[0];
                var password = params[1];
                LoginResponse loginResponse = server.login(username, password);
                authToken = loginResponse.authToken();
            }
        } catch (Exception ignore){
        }
        throw new IOException("Expected: <username> <password>");
    }

    public String logout(String authToken) throws IOException {
        server.logout(authToken);
        return "User logged out.";
    }

    public String listGames(String authToken) throws IOException {
        var gameResponse = server.listGames(authToken);
        var games = gameResponse.games();
        var result = new StringBuilder();
        var gson = new Gson();
        for (var game : games) {
            result.append(gson.toJson(game)).append('\n');
        }
        return result.toString();
    }

    public String createGame(String... params) throws IOException {
        try{
            if(params.length == 1){
                var gameName = params[0];
                server.createGame(authToken, gameName);
            }
        } catch (Exception ignore){
        }
        throw new IOException("Expected: <gameName>");
    }

    public String joinGame(String... params) throws IOException {
        try{
            if(params.length == 2){
                var teamColorParam = params[0];
                ChessGame.TeamColor teamColor = null;
                if(Objects.equals(teamColorParam, "WHITE")){
                    teamColor = ChessGame.TeamColor.WHITE;
                }
                else if(Objects.equals(teamColorParam, "BLACK")){
                    teamColor = ChessGame.TeamColor.BLACK;
                }
                var gameID  = params[1];

                server.joinGame(authToken, teamColor, Integer.parseInt(gameID));
            }
        } catch (Exception ignore){
        }
        throw new IOException("Expected: <WHITE|BLACK> <gameID>");
    }

    public String clear() throws IOException {
        server.clearApplication();
        return "Cleared everything";
    }

    public String help() {
        if(postLogin){
            return postLoginHelp();
        }
        else {
            return preLoginHelp();
        }
    }

    public String postLoginHelp() {
        return """
                create <gameName> - create a new chess game
                list - list active chess games
                join - <WHITE|BLACK> <gameID> - join a chess game
                logout - logout when finished
                quit - close the chess client
                help - receive a list of executable commands
                """;
    }

    public String preLoginHelp() {
        return """
                register <username> <password> <email> - create a new account
                login <username> <password> - login an existing user
                quit - close the chess client
                help - receive a list of executable commands
                """;
    }
}
