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
    private boolean lwCastlePossible;
    private boolean rwCastlePossible;
    private boolean lbCastlePossible;
    private boolean rbCastlePossible;
    private Collection<ChessPosition> doubleMovePawns;

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
        this.lwCastlePossible = true;
        this.rwCastlePossible = true;
        this.lbCastlePossible = true;
        this.rbCastlePossible = true;
        this.doubleMovePawns = new ArrayList<>();
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
    public void handleEnPassant(Collection<ChessMove> validMoves, ChessPiece piece, ChessPosition startPosition, int currentRow, int finalRow, TeamColor team){
        if(piece.getPieceType()== ChessPiece.PieceType.PAWN && piece.getTeamColor()==team && startPosition.getRow()==currentRow){
            if(startPosition.getColumn()-1>0){
                if(doubleMovePawns.contains(new ChessPosition(currentRow, startPosition.getColumn()-1))){
                    validMoves.add(new ChessMove(startPosition, new ChessPosition(finalRow, startPosition.getColumn()-1), null));
                }
            }
            if(startPosition.getColumn()+1<9){
                if(doubleMovePawns.contains(new ChessPosition(5, startPosition.getColumn()+1))){
                    validMoves.add(new ChessMove(startPosition, new ChessPosition(finalRow, startPosition.getColumn()+1), null));
                }
            }
        }
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
            handleCastling(validMoves);
            handleEnPassant(validMoves, piece, startPosition, startPosition.getRow(), startPosition.getRow()+1, TeamColor.WHITE);
            handleEnPassant(validMoves, piece, startPosition, startPosition.getRow(), startPosition.getRow()-1, TeamColor.BLACK);
            removeCheckMoves(validMoves);
            return validMoves;
        }
        return null;
    }

    public void removeCheckMoves(Collection<ChessMove> validMoves) {
        //remove moves that put their own king in check
        Iterator<ChessMove> iterator = validMoves.iterator();
        while(iterator.hasNext()) {
            ChessMove move = iterator.next();
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
    public boolean leftCastleBlocked(TeamColor teamColor, int row, int col){
        chessBoard.addPiece(new ChessPosition(row,col), new ChessPiece(teamColor, ChessPiece.PieceType.KING));
        chessBoard.addPiece(new ChessPosition(row, 5), null);
        updateIsInCheck();
        if(teamColor == TeamColor.WHITE && whiteCheck || teamColor == TeamColor.BLACK && blackCheck) {
            chessBoard.addPiece(new ChessPosition(row ,3), null);
            chessBoard.addPiece(new ChessPosition(row ,4), null);
            chessBoard.addPiece(new ChessPosition(row, 5), new ChessPiece(teamColor, ChessPiece.PieceType.KING));
            return true;
        }
        chessBoard.addPiece(new ChessPosition(row,col), null);
        chessBoard.addPiece(new ChessPosition(row, 5), new ChessPiece(teamColor, ChessPiece.PieceType.KING));
        return false;
    }
    public boolean rightCastleBlocked(TeamColor teamColor, int row, int col){
        chessBoard.addPiece(new ChessPosition(row,col), new ChessPiece(teamColor, ChessPiece.PieceType.KING));
        chessBoard.addPiece(new ChessPosition(row, 5), null);
        updateIsInCheck();
        if(teamColor == TeamColor.WHITE && whiteCheck || teamColor == TeamColor.BLACK && blackCheck) {
            chessBoard.addPiece(new ChessPosition(row ,6), null);
            chessBoard.addPiece(new ChessPosition(row ,7), null);
            chessBoard.addPiece(new ChessPosition(row, 5), new ChessPiece(teamColor, ChessPiece.PieceType.KING));
            return true;
        }
        chessBoard.addPiece(new ChessPosition(row,col), null);
        chessBoard.addPiece(new ChessPosition(row, 5), new ChessPiece(teamColor, ChessPiece.PieceType.KING));
        return false;
    }
    public void castlingHelper(int row, TeamColor teamColor, Collection<ChessMove> moves) {
        //check if king/rooks have moved
        ChessPiece king = chessBoard.getPiece(new ChessPosition(row,5));
        if(king != null && king.getPieceType()== ChessPiece.PieceType.KING && king.getTeamColor()==teamColor){
            ChessPiece leftRook = chessBoard.getPiece(new ChessPosition(row,1));
            //add left castle move if possible
            if(teamColor == TeamColor.WHITE && lwCastlePossible || teamColor == TeamColor.BLACK && lbCastlePossible) {
                if (leftRook != null && leftRook.getPieceType() == ChessPiece.PieceType.ROOK && leftRook.getTeamColor() == teamColor) {
                    if (chessBoard.getPiece(new ChessPosition(row, 2)) == null && chessBoard.getPiece(new ChessPosition(row, 3)) == null && chessBoard.getPiece(new ChessPosition(row, 4)) == null) {
                        for (int i = 3; i < 5; i++) {
                            if (leftCastleBlocked(teamColor, row, i)) {
                                return;
                            }
                        }
                        ChessMove leftCastleKing = new ChessMove(new ChessPosition(row, 5), new ChessPosition(row, 3), null);
                        moves.add(leftCastleKing);
                    }
                }
            }
            //add right castle move if possible
            if(teamColor == TeamColor.WHITE && rwCastlePossible || teamColor == TeamColor.BLACK && rbCastlePossible){
                ChessPiece rightRook = chessBoard.getPiece(new ChessPosition(row,8));
                if(rightRook != null && rightRook.getPieceType()== ChessPiece.PieceType.ROOK && rightRook.getTeamColor()==teamColor){
                    if(chessBoard.getPiece(new ChessPosition(row, 6))==null && chessBoard.getPiece(new ChessPosition(row, 7))==null){
                        for(int i = 7; i>5; i--){
                            if(rightCastleBlocked(teamColor, row, i)){
                                return;
                            }
                        }
                        ChessMove rightCastleKing = new ChessMove(new ChessPosition(row,5), new ChessPosition(row,7), null);
                        moves.add(rightCastleKing);
                    }
                }
            }
        }
    }
    public void handleCastling(Collection<ChessMove> moves){
        //check castling for white
        castlingHelper(1, TeamColor.WHITE, moves);
        //check castling for black
        castlingHelper(8, TeamColor.BLACK, moves);
    }

    public void updateFields(){
        updateIsInCheck();
        //if in check, see if in checkmate
        updateIsInCheckmate();
        checkStalemate();
    }
    public void rookCastleHelper(ChessMove move, int row, TeamColor teamColor){
        if(chessBoard.getPiece(move.getStartPosition()).getPieceType() == ChessPiece.PieceType.KING && chessBoard.getPiece(move.getStartPosition()).getTeamColor()==teamColor){
            //if true add left rook move
            if(move.getStartPosition().equals(new ChessPosition(row, 5)) && move.getEndPosition().equals(new ChessPosition(row, 3))) {
                chessBoard.addPiece(new ChessPosition(row,4), new ChessPiece(teamColor, ChessPiece.PieceType.ROOK));
                chessBoard.addPiece(new ChessPosition(row,1), null);
            }
            //if true add right rook move
            else if(move.getStartPosition().equals(new ChessPosition(row, 5)) && move.getEndPosition().equals(new ChessPosition(row, 7))){
                chessBoard.addPiece(new ChessPosition(row,6), new ChessPiece(teamColor, ChessPiece.PieceType.ROOK));
                chessBoard.addPiece(new ChessPosition(row,8), null);
            }
        }
    }
    public void rookCastleMove(ChessMove move){
        //check white castle
        rookCastleHelper(move, 1, TeamColor.WHITE);
        //check black castle
        rookCastleHelper(move, 8, TeamColor.BLACK);
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
            //check if move is castling to add rook move in addition to king move
            rookCastleMove(move);
            enPassantRemovePawn(movePiece, startPosition, move);
            chessBoard.addPiece(startPosition, null);
            //If promotion piece is given in the move, make a new chess piece of that type rather than a pawn
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
            setCastleFlags(startPosition);
            updateDoubleMovePawns(move, startPosition);
            updateFields();
        }
    }
    public void enPassantRemovePawn(ChessPiece movePiece, ChessPosition startPosition, ChessMove move){
        //checks if an enPassant move has occurred. If so, remove the captured pawn from the board.
        if(movePiece.getPieceType() == ChessPiece.PieceType.PAWN && doubleMovePawns.contains(new ChessPosition(startPosition.getRow(),move.getEndPosition().getColumn()))){
            chessBoard.addPiece(new ChessPosition(startPosition.getRow(),move.getEndPosition().getColumn()),null);
            doubleMovePawns.remove(new ChessPosition(startPosition.getRow(),move.getEndPosition().getColumn()));
        }
    }
    public void updateDoubleMovePawns(ChessMove move, ChessPosition startPosition){
        for(int i =1; i<9; i++){
            if(chessBoard.getPiece(move.getEndPosition()).getPieceType()== ChessPiece.PieceType.PAWN && (startPosition.equals(new ChessPosition(2, i)) && move.getEndPosition().equals(new ChessPosition(4, i)) || startPosition.equals(new ChessPosition(7, i)) && move.getEndPosition().equals(new ChessPosition(5, i)))){
                doubleMovePawns.add(move.getEndPosition());
            }
            else if(chessBoard.getPiece(move.getEndPosition()).getPieceType()== ChessPiece.PieceType.PAWN && startPosition.equals(new ChessPosition(4, i)) || startPosition.equals(new ChessPosition(5, i))){
                doubleMovePawns.remove(startPosition);
            }
        }
    }
    public void setCastleFlags(ChessPosition startPosition){
        if(startPosition.equals(new ChessPosition(1, 5))){
            rwCastlePossible = false;
            lwCastlePossible = false;
        }
        else if(startPosition.equals(new ChessPosition(8, 5))){
            rbCastlePossible = false;
            rwCastlePossible = false;
        }
        else if(startPosition.equals(new ChessPosition(1,8))){
            rwCastlePossible = false;
        }
        else if(startPosition.equals(new ChessPosition(1,1))){
            lwCastlePossible = false;
        }
        else if(startPosition.equals(new ChessPosition(8,1))){
            lbCastlePossible = false;
        }
        else if(startPosition.equals(new ChessPosition(8,8))){
            rbCastlePossible = false;
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
    public void resetCastleFlags(){
        lwCastlePossible = true;
        rwCastlePossible = true;
        lbCastlePossible = true;
        rbCastlePossible = true;
    }
    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        chessBoard = board;
        updateFields();
        resetCastleFlags();
        this.doubleMovePawns = new ArrayList<>();
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
