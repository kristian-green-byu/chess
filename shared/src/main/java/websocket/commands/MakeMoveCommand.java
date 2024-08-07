package websocket.commands;

import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand {

    private final ChessMove move;

    private final String authToken;

    private final Integer gameID;

    public MakeMoveCommand(ChessMove move, String authToken, Integer gameID) {
        super(CommandType.MAKE_MOVE, authToken, gameID);
        this.move = move;
        this.authToken = authToken;
        this.gameID = gameID;
    }

    public ChessMove getMove() {
        return move;
    }

    public String getAuthToken() {
        return authToken;
    }

    public Integer getGameID() {
        return gameID;
    }
}
