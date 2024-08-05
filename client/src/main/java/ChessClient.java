import chess.ChessBoard;
import chess.ChessGame;
import model.GameData;
import responses.LoginResponse;
import responses.RegisterResponse;
import serverfacade.ServerFacade;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import static ui.EscapeSequences.*;

public class ChessClient {
    private static ServerFacade server;
    private static String authToken;
    private boolean postLogin;
    private boolean inGame;

    public ChessClient(int port) {
        server = new ServerFacade("http://localhost:"+port);
        postLogin = false;
        inGame = false;
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
                    case "join" -> joinGame(params);
                    case "clear" -> clear();
                    case "quit" -> "quit";
                    case "help" -> help();
                    default -> "Invalid Command. Type help to see valid commands";
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
                return "Registration successful. You are now logged in as "+username+"\nType help to see new commands";
            }
            else{
                return "Expected: <username> <password> <email>";
            }
        } catch (Exception e){
            if(e.getMessage().equals("Request unsuccessful: 403")){
                return "Request unsuccessful. Verify your inputs and try again";
            }
            return e.getMessage();
        }
    }

    public String login(String... params) throws IOException {
        try{
            if(params.length == 2){
                var username = params[0];
                var password = params[1];
                LoginResponse loginResponse = server.login(username, password);
                authToken = loginResponse.authToken();
                postLogin = true;
                return "You are now logged in as "+username+"\nType help to see new commands";
            }
            else {
                return "Expected: <username> <password>";
            }
        } catch (Exception e){
            return e.getMessage();
        }
    }

    public String logout(String authToken) throws IOException {
        if(!postLogin){
            return "You are not logged in";
        }
        try{
            server.logout(authToken);
            return "Logged out successfully";
        }
        catch (Exception e){
            return e.getMessage();
        }
    }

    public String listGames(String authToken) throws IOException {
        if(!postLogin){
            return "Login first to see current games";
        }
        try{
            var gameResponse = server.listGames(authToken);
            var games = gameResponse.games();
            var result = new StringBuilder();
            int gameStringID = 1;
            for (var game : games) {
                result.append(gameStringID).append(". Name: ").append(game.gameName())
                        .append(" White Player: ").append(game.whiteUsername())
                        .append(" Black Player: ").append(game.blackUsername()).append('\n');
                gameStringID++;
            }
            return result.toString();
        }
        catch (Exception e){
            return e.getMessage();
        }
    }

    public String createGame(String... params) throws IOException {
        if(!postLogin){
            return "Login first to create a game";
        }
        try{
            if(params.length == 1){
                var gameName = params[0];
                server.createGame(authToken, gameName);
                return "Created game successfully";
            }
            else{
                return "Expected: <gameName>";
            }
        } catch (Exception e){
            return e.getMessage();
        }
    }

    public String joinGame(String... params) throws IOException {
        if(!postLogin){
            return "Login first to join a game";
        }
        try{
            if(params.length == 2){
                var teamColorParam = params[0];
                ChessGame.TeamColor teamColor;
                if(Objects.equals(teamColorParam, "white")){
                    teamColor = ChessGame.TeamColor.WHITE;
                }
                else if(Objects.equals(teamColorParam, "black")){
                    teamColor = ChessGame.TeamColor.BLACK;
                }
                else{
                    return "Please only write white to join " +
                            "as white and black to join as black\n"
                            +"Make sure you follow the format join <white|black> <gameNumber>";
                }
                if(!isNumeric(params[1])){
                    return "You did not input a valid integer for <gameNumber>. Please try again";
                }
                int desiredID  = Integer.parseInt(params[1]);
                var gameResponse = server.listGames(authToken);
                var games = gameResponse.games();
                int currentID = 1;
                int gameID = 0;
                GameData gameData = null;
                for (var game : games) {
                    if(desiredID == currentID){
                        gameID = game.gameID();
                        gameData = game;
                        break;
                    }
                    currentID++;
                }
                if(gameID == 0){
                    return "Invalid game number. Type list to see possible game numbers";
                }
                server.joinGame(authToken, teamColor, gameID);
                inGame = true;
                return "Joined game successfully as "+teamColor+"\n"+ displayBoard(gameData);
            }
            else{
                return "Expected: <white|black> <gameNumber>";
            }
        } catch (Exception e){
            return e.getMessage();
        }
    }

    private static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public String clear() throws IOException {
        server.clearApplication();
        postLogin = false;
        return "Cleared everything";
    }

    private String help() {
        if(postLogin){
            return postLoginHelp();
        }
        else {
            return preLoginHelp();
        }
    }
    
    private String displayBoard(GameData gameData){
        ChessGame game = gameData.game();
        ChessBoard board = game.getBoard();
        String boardString = board.toString();
        StringBuilder result = new StringBuilder();
        boolean alt = false;
        for (var line : boardString.split("\n")) {
            for(var character : line.toCharArray()) {
                if(character == '|'){
                    continue;
                }
                if(alt){
                    result.append(SET_BG_COLOR_DARK_GREY);
                    alt = false;
                }
                else{
                    result.append(SET_BG_COLOR_LIGHT_GREY);
                    alt = true;
                }

                if(character == ' '){
                    result.append(EMPTY);
                }
                else if(character == 'r'){
                    result.append(SET_TEXT_COLOR_BLACK + BLACK_ROOK);
                }
                else if(character == 'n'){
                    result.append(SET_TEXT_COLOR_BLACK + BLACK_KNIGHT);
                }
                else if(character == 'b'){
                    result.append(SET_TEXT_COLOR_BLACK + BLACK_BISHOP);
                }
                else if(character == 'q'){
                    result.append(SET_TEXT_COLOR_BLACK + BLACK_QUEEN);
                }
                else if(character == 'k'){
                    result.append(SET_TEXT_COLOR_BLACK + BLACK_KING);
                }
                else if(character == 'p'){
                    result.append(SET_TEXT_COLOR_BLACK + BLACK_PAWN);
                }
                else if(character == 'R'){
                    result.append(SET_TEXT_COLOR_WHITE + WHITE_ROOK);
                }
                else if(character == 'N'){
                    result.append(SET_TEXT_COLOR_WHITE + WHITE_KNIGHT);
                }
                else if(character == 'B'){
                    result.append(SET_TEXT_COLOR_WHITE + WHITE_BISHOP);
                }
                else if(character == 'Q'){
                    result.append(SET_TEXT_COLOR_WHITE + WHITE_QUEEN);
                }
                else if(character == 'K'){
                    result.append(SET_TEXT_COLOR_WHITE + WHITE_KING);
                }
                else if(character == 'P'){
                    result.append(SET_TEXT_COLOR_WHITE + WHITE_PAWN);
                }
            }
            result.append(RESET_BG_COLOR + '\n');
            alt = !alt;
        }
        return result.toString();
    }
    
    private String postLoginHelp() {
        return """
                create <gameName> - create a new chess game
                list - list active chess games
                join - <WHITE|BLACK> <gameNumber> - join a chess game; list the games to find game number
                logout - logout when finished
                quit - close the chess client
                help - receive a list of executable commands
                """;
    }

    private String preLoginHelp() {
        return """
                register <username> <password> <email> - create a new account
                login <username> <password> - login an existing user
                quit - close the chess client
                help - receive a list of executable commands
                """;
    }
}
