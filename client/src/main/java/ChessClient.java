
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;
import responses.LoginResponse;
import responses.RegisterResponse;
import serverfacade.ServerFacade;
import websocket.WebSocketFacade;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ui.EscapeSequences.*;

public class ChessClient {
    private static ServerFacade server;
    private static String authToken;
    private boolean postLogin;
    private boolean inGame;
    private String user;
    private int joinedGame;
    private WebSocketFacade ws;
    private final int port;
    private ChessGame.TeamColor color;

    public ChessClient(int port) {
        server = new ServerFacade("http://localhost:" + port);
        postLogin = false;
        inGame = false;
        user = null;
        joinedGame = 0;
        this.port = port;
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
                    case "quit" -> "quit";
                    case "help" -> help();
                    case "observe" -> observe(params);
                    case "leave" -> leave();
                    case "redraw" -> redraw();
                    case "move" -> move(params);
                    default -> "Invalid Command. Type help to see valid commands";
                };
            }
        } catch (IOException e) {
            result = e.getMessage();
        }
        return result;
    }

    public String register(String... params) throws IOException {
        if (inGame) {
            return "Leave your game first to complete this request";
        }
        if(postLogin){
            return "You're already logged in";
        }
        try{
            if(params.length == 3){
                String username = params[0];
                String password = params[1];
                String email = params[2];
                RegisterResponse registerResponse = server.register(username, password, email);
                authToken = registerResponse.authToken();
                postLogin = true;
                user = username;
                return "Registration successful. You are now logged in as "+username+"\nType help to see new commands";
            }
            else{
                return "Expected: <username> <password> <email>";
            }
        } catch (Exception e){
            if(e.getMessage().equals("403")){
                return "User already taken";
            }
            else {
                return "Request failed. Verify your inputs and try again";
            }
        }
    }

    public String login(String... params) throws IOException {
        if (inGame) {
            return "Leave your game first to complete this request";
        }
        if(postLogin){
            return "You're already logged in";
        }
        try{
            if(params.length == 2){
                var username = params[0];
                var password = params[1];
                LoginResponse loginResponse = server.login(username, password);
                authToken = loginResponse.authToken();
                postLogin = true;
                user = username;
                return "You are now logged in as "+username+"\nType help to see new commands";
            }
            else {
                return "Expected: <username> <password>";
            }
        } catch (Exception e){
            if(e.getMessage().equals("401")){
                return "Username or password is invalid. Please verify your inputs and try again";
            }
            else{
                return "Request failed. Verify your inputs and try again";
            }
        }
    }

    public String logout(String authToken) throws IOException {
        if (inGame) {
            return "Leave your game first to complete this request";
        }
        if(!postLogin){
            return "You are not logged in";
        }
        try{
            server.logout(authToken);
            postLogin = false;
            user = null;
            return "Logged out successfully";
        }
        catch (Exception e){
            if(e.getMessage().equals("401")){
                return "You are not logged in";
            }
            else{
                return "Request failed. Verify your inputs and try again";
            }
        }
    }

    public String listGames(String authToken) throws IOException {
        if (inGame) {
            return "Leave your game first to complete this request";
        }
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
            if(games.isEmpty()){
                return "There are no currently no games.";
            }
            return result.toString();
        }
        catch (Exception e){
            if(e.getMessage().equals("401")){
                return "You are not logged in";
            }
            else {
                return "Request failed. Verify your inputs and try again";
            }
        }
    }

    public String createGame(String... params) throws IOException {
        if (inGame) {
            return "Leave your game first to complete this request";
        }
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
            if(e.getMessage().equals("401")){
                return "Your request is invalid. Ensure you are logged in and chose a valid name";
            }
            else{
                return "Request failed. Verify your inputs and try again";
            }
        }
    }

    public String joinGame(String... params) throws IOException {
        if (inGame) {
            return "Leave your game first to complete this request";
        }
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
                if(isNotNumeric(params[1])){
                    return "You did not input a valid integer for <gameNumber>. Please try again";
                }
                int desiredID  = Integer.parseInt(params[1]);
                GameData gameData = getGameData(desiredID);
                if(gameData == null){
                    return "Invalid game number. Type list to see possible game numbers";
                }
                server.joinGame(authToken, teamColor, gameData.gameID());
                ws = new WebSocketFacade("http://localhost:" + port, teamColor);
                ws.joinGame(authToken, gameData.gameID());
                inGame = true;
                postLogin = false;
                joinedGame = desiredID;
                color = teamColor;
                Thread.sleep(500);
                return "Joined game " + desiredID +" successfully.";
            }
            else{
                return "Expected: <white|black> <gameNumber>";
            }
        } catch (Exception e){
            if(e.getMessage().equals("401")){
                return "You are not logged in";
            }
            else if(e.getMessage().equals("400")){
                return "Game doesn't exist";
            }
            else if(e.getMessage().equals("403")){
                return "There is already a player of your requested color";
            }
            else{
                return "Request failed. Verify your inputs and try again";
            }
        }
    }

    private static boolean isNotNumeric(String str) {
        try {
            Double.parseDouble(str);
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    public String help() {
        if(postLogin){
            return postLoginHelp();
        }
        else if(!inGame) {
            return preLoginHelp();
        }
        else {
            return inGameHelp();
        }
    }

    public String observe(String... params) throws IOException {
        if(!postLogin){
            return "Login first to observe a game";
        }
        if (inGame) {
            return "Leave your game first to complete this request";
        }
        try{
            if(params.length == 1){
                if(isNotNumeric(params[0])){
                    return "You did not input a valid integer for <gameNumber>. Please try again";
                }
                int desiredID  = Integer.parseInt(params[0]);
                GameData gameData = getGameData(desiredID);
                if (gameData == null){
                    return "Invalid game number. Type list to see possible game numbers";
                }
                inGame = true;
                postLogin = false;
                joinedGame = desiredID;
                return "Observing game "+desiredID+"\nType help to see new commands."+"\n\n"+
                        //displayBoard(gameData, ChessGame.TeamColor.WHITE)+
                        SET_BG_COLOR_BLACK + SET_TEXT_COLOR_BLACK+
                        "                              "+ RESET_BG_COLOR + '\n';
                        //displayBoard(gameData, ChessGame.TeamColor.BLACK);
            }
            else{
                return "Expected: <gameNumber>";
            }
        } catch (Exception e){
            if(e.getMessage().equals("401")){
                return "You are not logged in";
            }
            else {
                return "Request failed. Verify your inputs and try again";
            }
        }
    }

    public String leave() throws IOException {
        if (!inGame) {
            return "Join a game first to complete this request";
        }
        postLogin = true;
        inGame = false;
        return "Placeholder";
    }

    public String redraw() throws IOException {
        if (!inGame) {
            return "Join a game first to redraw the board";
        }
        GameData gameData = getGameData(joinedGame);
        if(Objects.equals(Objects.requireNonNull(gameData).whiteUsername(), user)){
            return "Redrawing the board...\n"; //displayBoard(gameData, ChessGame.TeamColor.WHITE);
        }
        else if(Objects.equals(Objects.requireNonNull(gameData).blackUsername(), user)){
            return "Redrawing the board...\n"; //displayBoard(gameData, ChessGame.TeamColor.BLACK);
        }
        else {
            return "Redrawing the board...\n"; //displayBoard(gameData, ChessGame.TeamColor.WHITE)+
                    //SET_BG_COLOR_BLACK + SET_TEXT_COLOR_BLACK+
                    //"                              "+ RESET_BG_COLOR + '\n';
                    //displayBoard(gameData, ChessGame.TeamColor.BLACK);
        }
    }

    public String move(String... params) throws IOException {
        if(!inGame){
            return "Join a game first to make a move";
        }
        String fromString = params[0];
        String toString = params[1];
        if(invalidMoveFormat(fromString, toString)){
            return "Move formated incorrectly. Please enter your moves in the format [a-h][1-8] like c4, for instance";
        }
        ChessGame chessGame = ws.getChessGame();
        ChessPosition fromPos = makePosition(fromString);
        ChessPosition toPos = makePosition(toString);
        Collection<ChessMove> validMoves = chessGame.validMoves(fromPos);
        ChessPiece promotionPiece;
        ChessPiece currentPiece = chessGame.getBoard().getPiece(fromPos);
        ChessGame.TeamColor teamColor = currentPiece.getTeamColor();
        if(color != teamColor){
            return "You cannot move your opponent's pieces";
        }
        ChessMove move = new ChessMove(fromPos, toPos, null);
        for(ChessMove posMov : validMoves){
            if(posMov.getPromotionPiece()!=null){
                Scanner scanner = new Scanner(System.in);
                System.out.print("Promotion possible! Type in the name of the piece you want to promote to"+
                        SET_TEXT_BLINKING + RESET_TEXT_COLOR + "\n" + RESET_TEXT_BOLD_FAINT
                        + ">>> " + SET_TEXT_COLOR_GREEN);
                String pieceString = scanner.nextLine();
                promotionPiece = getPromotionPiece(pieceString, teamColor);
                if(promotionPiece == null){
                    return "You entered an invalid promotion piece";
                }
                move = new ChessMove(fromPos, toPos, promotionPiece.getPieceType());
            }
        }
        if(!validMoves.contains(move)){
            return "Move is not possible";
        }
        return null;
    }

    private ChessPiece getPromotionPiece(String pieceString, ChessGame.TeamColor teamColor){
        ChessPiece promotionPiece;
        pieceString = pieceString.toLowerCase();
        switch(pieceString){
            case "queen" -> promotionPiece = new ChessPiece(teamColor, ChessPiece.PieceType.QUEEN);
            case "rook" -> promotionPiece = new ChessPiece(teamColor, ChessPiece.PieceType.ROOK);
            case "knight" -> promotionPiece = new ChessPiece(teamColor, ChessPiece.PieceType.KNIGHT);
            case "bishop" -> promotionPiece = new ChessPiece(teamColor, ChessPiece.PieceType.BISHOP);
            default -> promotionPiece = null;
        }
        return promotionPiece;
    }

    private static ChessPosition makePosition(String string) {
        char row = string.charAt(1);
        char colChar = string.charAt(0);
        int col = 0;
        switch(colChar){
            case 'a' -> col = 1;
            case 'b' -> col = 2;
            case 'c' -> col = 3;
            case 'd' -> col = 4;
            case 'e' -> col = 5;
            case 'f' -> col = 6;
            case 'g' -> col = 7;
            case 'h' -> col = 8;
        }
        return new ChessPosition(row, col);
    }

    private static boolean invalidMoveFormat(String fromString, String toString) {
        String regex = "[a-h][1-8]";
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matchFrom = pattern.matcher(fromString);
        Matcher matchTo = pattern.matcher(toString);
        if(!matchFrom.find()){
            return false;
        }
        else return matchTo.find();
    }

    private static GameData getGameData(int desiredID) throws IOException {
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
            return null;
        }
        return gameData;
    }
    
    private String postLoginHelp() {
        return """
                create <gameName> - create a new chess game
                list - list active chess games
                join <WHITE|BLACK> <gameNumber> - join a chess game; list the games to find the game number
                observe <gameNumber> - observe a chess game without playing; list the games to find the game number
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

    private String inGameHelp() {
        return """
                redraw - refresh the chessboard
                leave - leave the game
                move <from> <to> - make a chess move
                resign - forfeit the game without leaving
                highlight <coordinate> - see legal moves for a given piece
                quit - close the chess client
                help - receive a list of executable commands
                """;
    }
}
