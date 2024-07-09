package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard chessBoard;
    private TeamColor teamTurn;
    private boolean whiteCheck;
    private boolean blackCheck;
    private boolean whiteCheckmate;
    private boolean blackCheckmate;
    private boolean whiteStalemate;
    private boolean blackStalemate;

    public ChessGame() {
        //Initializes all chess parameters to their defaults.
        //Check, checkmate, and stalemate are all false by default.
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        this.chessBoard = board;
        this.teamTurn = TeamColor.WHITE;
        this.whiteCheck = false;
        this.blackCheck = false;
        this.whiteCheckmate = false;
        this.blackCheckmate = false;
        this.whiteStalemate = false;
        this.blackStalemate = false;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = chessBoard.getPiece(startPosition);
        if(piece != null) {
            Collection<ChessMove> validMoves = piece.pieceMoves(chessBoard, startPosition);
            Iterator<ChessMove> iterator = validMoves.iterator();
            while(iterator.hasNext()) {
                ChessMove move = iterator.next();
                //remove moves that put their own king in check
                ChessPiece wantedPiece = chessBoard.getPiece(move.getStartPosition());
                ChessPiece replacePiece = chessBoard.getPiece(move.getEndPosition());
                chessBoard.addPiece(move.getStartPosition(), null);
                chessBoard.addPiece(move.getEndPosition(), wantedPiece);
                updateIsInCheck();
                if(wantedPiece.getTeamColor() == TeamColor.WHITE && whiteCheck || wantedPiece.getTeamColor() == TeamColor.BLACK && blackCheck){
                    iterator.remove();
                }
                chessBoard.addPiece(move.getEndPosition(),replacePiece);
                chessBoard.addPiece(move.getStartPosition(), wantedPiece);
                updateIsInCheck();
            }
            return validMoves;
        }
        return null;
    }

    public void updateIsInCheck() {
        whiteCheck = false;
        blackCheck = false;
        for(int i = 8; i >= 1; i--) {
            for(int j =1; j < 9; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = chessBoard.getPiece(position);
                if(piece != null) {
                    Collection<ChessMove> moves = piece.pieceMoves(chessBoard, position);
                    for(ChessMove move : moves) {
                        if(chessBoard.getPiece(move.getEndPosition()) != null){
                            if(chessBoard.getPiece(move.getEndPosition()).getPieceType() == ChessPiece.PieceType.KING && chessBoard.getPiece(move.getEndPosition()).getTeamColor() != chessBoard.getPiece(move.getStartPosition()).getTeamColor()){
                                if(chessBoard.getPiece(move.getEndPosition()).getTeamColor() == TeamColor.WHITE) {
                                    whiteCheck = true;
                                }
                                else if(chessBoard.getPiece(move.getEndPosition()).getTeamColor() == TeamColor.BLACK) {
                                    blackCheck = true;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void updateIsInCheckmate() {
        updateIsInCheck();
        if(whiteCheck) {
            whiteCheckmate = true;
        }
        else if(blackCheck) {
            blackCheckmate = true;
        }
        for(int i = 8; i >= 1; i--) {
            for(int j =1; j < 9; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = chessBoard.getPiece(position);
                if(piece != null) {
                    Collection<ChessMove> moves = piece.pieceMoves(chessBoard, position);
                    for(ChessMove move : moves) {
                        ChessPiece wantedPiece = chessBoard.getPiece(move.getStartPosition());
                        if(whiteCheckmate && wantedPiece.getTeamColor() == TeamColor.WHITE || blackCheckmate && wantedPiece.getTeamColor() == TeamColor.BLACK) {
                            ChessPiece replacePiece = chessBoard.getPiece(move.getEndPosition());
                            chessBoard.addPiece(move.getStartPosition(), null);
                            chessBoard.addPiece(move.getEndPosition(), wantedPiece);
                            updateIsInCheck();
                            chessBoard.addPiece(move.getEndPosition(),replacePiece);
                            chessBoard.addPiece(move.getStartPosition(), wantedPiece);
                            if(whiteCheckmate && !whiteCheck) {
                                whiteCheckmate = false;
                                whiteCheck = true;
                                return;
                            }
                            else if(blackCheckmate && !blackCheck) {
                                blackCheckmate = false;
                                blackCheck = true;
                                return;
                            }
                        }
                    }
                }
            }
        }
    }
    public void checkStalemate(){
        if(whiteCheckmate || blackCheckmate) {
            return;
        }
        whiteStalemate = true;
        blackStalemate = true;
        for(int i = 8; i >= 1; i--) {
            for(int j =1; j < 9; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = chessBoard.getPiece(position);
                if(piece!=null){
                    Collection<ChessMove> moves = validMoves(position);
                    if(!moves.isEmpty()){
                        if(piece.getTeamColor()==TeamColor.WHITE){
                            whiteStalemate = false;
                        }
                        else{
                            blackStalemate = false;
                        }
                    }
                }
            }
        }
    }

    public void updateFields(){
        updateIsInCheck();
        //if in check, see if in checkmate
        updateIsInCheckmate();
        checkStalemate();
    }
    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPosition = move.getStartPosition();
        Collection<ChessMove> validMoves = validMoves(startPosition);
        if(validMoves == null) {
            throw new InvalidMoveException();
        }
        else if(!validMoves.contains(move)) {
            throw new InvalidMoveException();
        }
        else{
            ChessPiece movePiece = chessBoard.getPiece(startPosition);
            if(movePiece.getTeamColor() != teamTurn) {
                throw new InvalidMoveException();
            }
            chessBoard.addPiece(startPosition, null);
            if(move.getPromotionPiece()==null){
                chessBoard.addPiece(move.getEndPosition(), movePiece);
            }
            else{
                chessBoard.addPiece(move.getEndPosition(), new ChessPiece(movePiece.getTeamColor(), move.getPromotionPiece()));
            }

            //set next turn
            if(movePiece.getTeamColor() == TeamColor.WHITE) {
                teamTurn = TeamColor.BLACK;
            }
            else{
                teamTurn = TeamColor.WHITE;
            }
            updateFields();
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        if(teamColor == TeamColor.WHITE) {
            return whiteCheck;
        }
        else{
            return blackCheck;
        }
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if(teamColor == TeamColor.WHITE) {
            return whiteCheckmate;
        }
        else {
            return blackCheckmate;
        }
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if(teamColor == TeamColor.WHITE) {
            return whiteStalemate;
        }
        else{
            return blackStalemate;
        }
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        chessBoard = board;
        updateFields();
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return chessBoard;
    }
}
