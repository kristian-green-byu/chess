package websocket;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import model.GameData;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static ui.EscapeSequences.*;

public class WebSocketFacade extends Endpoint {

    Session session;
    private ChessGame.TeamColor teamColor = null;
    private ChessGame chessGame;

    public WebSocketFacade(String url) throws IOException {
        try {
            url = url.replace("http", "ws");
            URI wsUri = new URI(url + "/ws");
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, wsUri);
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    switch(serverMessage.getServerMessageType()){
                        case NOTIFICATION -> receiveNotification(message);
                        case LOAD_GAME -> receiveLoadGame(message);
                        case ERROR -> receiveError(message);
                    }
                }
            });
        } catch (DeploymentException | URISyntaxException e) {
            throw new IOException(e.getMessage());
        }
    }

    public void setTeamColor(ChessGame.TeamColor teamColor) {
        this.teamColor = teamColor;
    }

    public void joinGame(String authToken, Integer gameID) throws IOException {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (Exception ex) {
            throw new IOException(ex.getMessage());
        }
    }

    public void makeMove(String authToken, Integer gameID, ChessMove move) throws IOException {
        try {
            var command = new MakeMoveCommand(move, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (Exception ex) {
            throw new IOException(ex.getMessage());
        }
    }

    public void resign(String authToken, Integer gameID) throws IOException {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (Exception ex) {
            throw new IOException(ex.getMessage());
        }
    }

    public void leave(String authToken, Integer gameID) throws IOException {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (Exception ex) {
            throw new IOException(ex.getMessage());
        }
    }

    public ChessGame getChessGame() {
        return chessGame;
    }

    private void receiveNotification(String message) {
        NotificationMessage notificationMessage = new Gson().fromJson(message, NotificationMessage.class);
        System.out.print(ERASE_LINE);
        System.out.println(SET_TEXT_COLOR_MAGENTA+'\n'+notificationMessage.getMessage());
        System.out.print(SET_TEXT_BLINKING + RESET_TEXT_COLOR + "\n" + RESET_TEXT_BOLD_FAINT + ">>> " + SET_TEXT_COLOR_GREEN);
    }

    private void receiveLoadGame(String message) {
        LoadGameMessage loadGameMessage = new Gson().fromJson(message, LoadGameMessage.class);
        GameData game = loadGameMessage.getGame();
        chessGame = game.game();
        if(teamColor==null){
            String board1 = displayBoard(game, ChessGame.TeamColor.WHITE);
            System.out.print(ERASE_SCREEN);
            System.out.println('\n'+board1);
            String board2 = displayBoard(game, ChessGame.TeamColor.BLACK);
            System.out.print(ERASE_SCREEN);
            System.out.println('\n'+board2);
            return;
        }
        String board = displayBoard(game, teamColor);
        System.out.print(ERASE_SCREEN);
        System.out.println('\n'+board);
    }

    private void receiveError(String message) {
        ErrorMessage errorMessage = new Gson().fromJson(message, ErrorMessage.class);
        System.out.print(ERASE_LINE);
        System.out.println(SET_TEXT_COLOR_RED+'\n'+errorMessage.getErrorMessage());
    }

    private String displayBoard(GameData gameData, ChessGame.TeamColor color){
        ChessGame game = gameData.game();
        ChessBoard board = game.getBoard();
        String boardString = board.toString();
        StringBuilder result = new StringBuilder();
        boolean alt = false;
        String topBorder = SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK + EMPTY +
                " a  b  c  d  e  f  g  h " + EMPTY + RESET_BG_COLOR + '\n';
        int colNum = 8;
        if(color == ChessGame.TeamColor.BLACK){
            StringBuilder reverseBoardString = new StringBuilder(boardString);
            reverseBoardString.reverse();
            reverseBoardString.delete(0, 1);
            reverseBoardString.append('\n');
            boardString = reverseBoardString.toString();
            topBorder = SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK + EMPTY +
                    " h  g  f  e  d  c  b  a " + EMPTY + RESET_BG_COLOR + '\n';
            colNum = 1;
        }
        result.append(topBorder);
        for (var line : boardString.split("\n")) {
            result.append(SET_BG_COLOR_WHITE).append(SET_TEXT_COLOR_BLACK).append(' ').append(colNum).append(' ');
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
            result.append(SET_BG_COLOR_WHITE).append(SET_TEXT_COLOR_BLACK).append(' ').append(colNum).append(' ');
            if(color == ChessGame.TeamColor.WHITE){
                colNum--;
            }
            else {
                colNum++;
            }
            result.append(RESET_BG_COLOR + '\n');
            alt = !alt;
        }
        result.append(topBorder);
        return result.toString();
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}
