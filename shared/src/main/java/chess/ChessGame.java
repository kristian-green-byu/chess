package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

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

    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    private void handleEnPassant(Collection<ChessMove> validMoves, ChessPiece piece, ChessPosition startPosition, int finalRow, TeamColor team) {
        int currentRow = startPosition.getRow();
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN && piece.getTeamColor() == team && startPosition.getRow() == currentRow) {
            if (startPosition.getColumn() - 1 > 0) {
                if (doubleMovePawns.contains(new ChessPosition(currentRow, startPosition.getColumn() - 1))) {
                    validMoves.add(new ChessMove(startPosition, new ChessPosition(finalRow, startPosition.getColumn() - 1), null));
                }
            }
            if (startPosition.getColumn() + 1 < 9) {
                if (doubleMovePawns.contains(new ChessPosition(currentRow, startPosition.getColumn() + 1))) {
                    validMoves.add(new ChessMove(startPosition, new ChessPosition(finalRow, startPosition.getColumn() + 1), null));
                }
            }
        }
    }

    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = chessBoard.getPiece(startPosition);
        if (piece != null) {
            Collection<ChessMove> validMoves = piece.pieceMoves(chessBoard, startPosition);
            handleCastling(validMoves);
            handleEnPassant(validMoves, piece, startPosition,startPosition.getRow() + 1, TeamColor.WHITE);
            handleEnPassant(validMoves, piece, startPosition,startPosition.getRow() - 1, TeamColor.BLACK);
            removeCheckMoves(validMoves);
            return validMoves;
        }
        return null;
    }

    private void removeCheckMoves(Collection<ChessMove> validMoves) {
        //remove moves that put their own king in check
        Iterator<ChessMove> iterator = validMoves.iterator();
        while (iterator.hasNext()) {
            ChessMove move = iterator.next();
            ChessPiece wantedPiece = chessBoard.getPiece(move.getStartPosition());
            ChessPiece replacePiece = chessBoard.getPiece(move.getEndPosition());
            chessBoard.addPiece(move.getStartPosition(), null);
            chessBoard.addPiece(move.getEndPosition(), wantedPiece);
            updateIsInCheck();
            if (wantedPiece.getTeamColor() == TeamColor.WHITE && whiteCheck || wantedPiece.getTeamColor() == TeamColor.BLACK && blackCheck) {
                iterator.remove();
            }
            chessBoard.addPiece(move.getEndPosition(), replacePiece);
            chessBoard.addPiece(move.getStartPosition(), wantedPiece);
            updateIsInCheck();
        }
    }

    private void updateIsInCheck() {
        whiteCheck = false;
        blackCheck = false;
        for (int i = 8; i >= 1; i--) {
            for (int j = 1; j < 9; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = chessBoard.getPiece(position);
                if (piece != null) {
                    checkCheck(piece, position);
                }
            }
        }
    }

    private void checkCheck(ChessPiece piece, ChessPosition position) {
        Collection<ChessMove> moves = piece.pieceMoves(chessBoard, position);
        for (ChessMove move : moves) {
            if (chessBoard.getPiece(move.getEndPosition()) != null) {
                checkIfKingInMoves(move);
            }
        }
    }

    private void checkIfKingInMoves(ChessMove move) {
        if (enemyKingReachable(move)) {
            if (chessBoard.getPiece(move.getEndPosition()).getTeamColor() == TeamColor.WHITE) {
                whiteCheck = true;
            } else if (chessBoard.getPiece(move.getEndPosition()).getTeamColor() == TeamColor.BLACK) {
                blackCheck = true;
            }
        }
    }

    private boolean enemyKingReachable(ChessMove move) {
        ChessPiece endPiece = chessBoard.getPiece(move.getEndPosition());
        ChessPiece startPiece = chessBoard.getPiece(move.getStartPosition());
        if(endPiece.getPieceType() != ChessPiece.PieceType.KING){
            return false;
        } else {
            return endPiece.getTeamColor() != startPiece.getTeamColor();
        }
    }

    private void updateIsInCheckmate() {
        updateIsInCheck();
        if (whiteCheck) {
            whiteCheckmate = true;
        } else if (blackCheck) {
            blackCheckmate = true;
        }
        for (int i = 8; i >= 1; i--) {
            for (int j = 1; j < 9; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = chessBoard.getPiece(position);
                if (piece != null) {
                    if (checkmateUpdated(piece, position)){
                        return;
                    }
                }
            }
        }
    }

    private boolean checkmateUpdated(ChessPiece piece, ChessPosition position) {
        Collection<ChessMove> moves = piece.pieceMoves(chessBoard, position);
        for (ChessMove move : moves) {
            ChessPiece wantedPiece = chessBoard.getPiece(move.getStartPosition());
            TeamColor wantedColor = wantedPiece.getTeamColor();
            if (whiteCheckmate && wantedColor == TeamColor.WHITE || blackCheckmate && wantedColor == TeamColor.BLACK) {
                ChessPiece replacePiece = chessBoard.getPiece(move.getEndPosition());
                chessBoard.addPiece(move.getStartPosition(), null);
                chessBoard.addPiece(move.getEndPosition(), wantedPiece);
                updateIsInCheck();
                chessBoard.addPiece(move.getEndPosition(), replacePiece);
                chessBoard.addPiece(move.getStartPosition(), wantedPiece);
                if (whiteCheckmate && !whiteCheck) {
                    whiteCheckmate = false;
                    whiteCheck = true;
                    return true;
                } else if (blackCheckmate && !blackCheck) {
                    blackCheckmate = false;
                    blackCheck = true;
                    return true;
                }
            }
        }
        return false;
    }

    private void checkStalemate() {
        if (whiteCheckmate || blackCheckmate) {
            return;
        }
        whiteStalemate = true;
        blackStalemate = true;
        for (int i = 8; i >= 1; i--) {
            for (int j = 1; j < 9; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = chessBoard.getPiece(position);
                if (piece != null) {
                    checkStalemateFromPiece(position, piece);
                }
            }
        }
    }

    private void checkStalemateFromPiece(ChessPosition position, ChessPiece piece) {
        Collection<ChessMove> moves = validMoves(position);
        if (!moves.isEmpty()) {
            if (piece.getTeamColor() == TeamColor.WHITE) {
                whiteStalemate = false;
            } else {
                blackStalemate = false;
            }
        }
    }

    private boolean leftCastleBlocked(TeamColor teamColor, int row, int col) {
        chessBoard.addPiece(new ChessPosition(row, col), new ChessPiece(teamColor, ChessPiece.PieceType.KING));
        chessBoard.addPiece(new ChessPosition(row, 5), null);
        updateIsInCheck();
        if (teamColor == TeamColor.WHITE && whiteCheck || teamColor == TeamColor.BLACK && blackCheck) {
            chessBoard.addPiece(new ChessPosition(row, 3), null);
            chessBoard.addPiece(new ChessPosition(row, 4), null);
            chessBoard.addPiece(new ChessPosition(row, 5), new ChessPiece(teamColor, ChessPiece.PieceType.KING));
            return true;
        }
        chessBoard.addPiece(new ChessPosition(row, col), null);
        chessBoard.addPiece(new ChessPosition(row, 5), new ChessPiece(teamColor, ChessPiece.PieceType.KING));
        return false;
    }

    private boolean rightCastleBlocked(TeamColor teamColor, int row, int col) {
        chessBoard.addPiece(new ChessPosition(row, col), new ChessPiece(teamColor, ChessPiece.PieceType.KING));
        chessBoard.addPiece(new ChessPosition(row, 5), null);
        updateIsInCheck();
        if (teamColor == TeamColor.WHITE && whiteCheck || teamColor == TeamColor.BLACK && blackCheck) {
            chessBoard.addPiece(new ChessPosition(row, 6), null);
            chessBoard.addPiece(new ChessPosition(row, 7), null);
            chessBoard.addPiece(new ChessPosition(row, 5), new ChessPiece(teamColor, ChessPiece.PieceType.KING));
            return true;
        }
        chessBoard.addPiece(new ChessPosition(row, col), null);
        chessBoard.addPiece(new ChessPosition(row, 5), new ChessPiece(teamColor, ChessPiece.PieceType.KING));
        return false;
    }

    private void castlingHelper(int row, TeamColor teamColor, Collection<ChessMove> moves) {
        //check if king/rooks have moved
        ChessPiece king = chessBoard.getPiece(new ChessPosition(row, 5));
        if (king != null && king.getPieceType() == ChessPiece.PieceType.KING && king.getTeamColor() == teamColor) {
            ChessPiece leftRook = chessBoard.getPiece(new ChessPosition(row, 1));
            //add left castle move if possible
            if (teamColor == TeamColor.WHITE && lwCastlePossible || teamColor == TeamColor.BLACK && lbCastlePossible) {
                if (leftRook != null && leftRook.getPieceType() == ChessPiece.PieceType.ROOK && leftRook.getTeamColor() == teamColor) {
                    if (leftCastlePossible(row, teamColor, moves)) {
                        return;
                    }
                }
            }
            //add right castle move if possible
            if (teamColor == TeamColor.WHITE && rwCastlePossible || teamColor == TeamColor.BLACK && rbCastlePossible) {
                ChessPiece rightRook = chessBoard.getPiece(new ChessPosition(row, 8));
                if (rightRook != null && rightRook.getPieceType() == ChessPiece.PieceType.ROOK && rightRook.getTeamColor() == teamColor) {
                    rightCastlePossible(row, teamColor, moves);
                }
            }
        }
    }

    private void rightCastlePossible(int row, TeamColor teamColor, Collection<ChessMove> moves) {
        if (chessBoard.getPiece(new ChessPosition(row, 6)) == null && chessBoard.getPiece(new ChessPosition(row, 7)) == null) {
            for (int i = 7; i > 5; i--) {
                if (rightCastleBlocked(teamColor, row, i)) {
                    return;
                }
            }
            ChessMove rightCastleKing = new ChessMove(new ChessPosition(row, 5), new ChessPosition(row, 7), null);
            moves.add(rightCastleKing);
        }
    }

    private boolean leftCastlePossible(int row, TeamColor teamColor, Collection<ChessMove> moves) {
        if (castleNotBlocked(row)) {
            for (int i = 3; i < 5; i++) {
                if (leftCastleBlocked(teamColor, row, i)) {
                    return true;
                }
            }
            ChessMove leftCastleKing = new ChessMove(new ChessPosition(row, 5), new ChessPosition(row, 3), null);
            moves.add(leftCastleKing);
        }
        return false;
    }

    private boolean castleNotBlocked(int row) {
        for(int i = 2; i <5; i++){
            if(chessBoard.getPiece(new ChessPosition(row, i)) != null){
                return false;
            }
        }
        return true;
    }

    private void handleCastling(Collection<ChessMove> moves) {
        //check castling for white
        castlingHelper(1, TeamColor.WHITE, moves);
        //check castling for black
        castlingHelper(8, TeamColor.BLACK, moves);
    }

    private void updateFields() {
        updateIsInCheck();
        //if in check, see if in checkmate
        updateIsInCheckmate();
        checkStalemate();
    }

    private void rookCastleHelper(ChessMove move, int row, TeamColor teamColor) {
        ChessPiece.PieceType pieceType = chessBoard.getPiece(move.getStartPosition()).getPieceType();
        TeamColor pieceTeamColor = chessBoard.getPiece(move.getStartPosition()).getTeamColor();
        if(pieceType == ChessPiece.PieceType.KING && pieceTeamColor == teamColor) {
            //if true add left rook move
            if (move.getStartPosition().equals(new ChessPosition(row, 5)) && move.getEndPosition().equals(new ChessPosition(row, 3))) {
                chessBoard.addPiece(new ChessPosition(row, 4), new ChessPiece(teamColor, ChessPiece.PieceType.ROOK));
                chessBoard.addPiece(new ChessPosition(row, 1), null);
            }
            //if true add right rook move
            else if (move.getStartPosition().equals(new ChessPosition(row, 5)) && move.getEndPosition().equals(new ChessPosition(row, 7))) {
                chessBoard.addPiece(new ChessPosition(row, 6), new ChessPiece(teamColor, ChessPiece.PieceType.ROOK));
                chessBoard.addPiece(new ChessPosition(row, 8), null);
            }
        }
    }

    private void rookCastleMove(ChessMove move) {
        //check white castle
        rookCastleHelper(move, 1, TeamColor.WHITE);
        //check black castle
        rookCastleHelper(move, 8, TeamColor.BLACK);
    }

    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPosition = move.getStartPosition();
        Collection<ChessMove> validMoves = validMoves(startPosition);
        if (validMoves == null) {
            throw new InvalidMoveException();
        } else if (!validMoves.contains(move)) {
            throw new InvalidMoveException();
        } else {
            ChessPiece movePiece = chessBoard.getPiece(startPosition);
            if (movePiece.getTeamColor() != teamTurn) {
                throw new InvalidMoveException();
            }
            //check if move is castling to add rook move in addition to king move
            rookCastleMove(move);
            enPassantRemovePawn(movePiece, startPosition, move);
            chessBoard.addPiece(startPosition, null);
            //If promotion piece is given in the move, make a new chess piece of that type rather than a pawn
            if (move.getPromotionPiece() == null) {
                chessBoard.addPiece(move.getEndPosition(), movePiece);
            } else {
                chessBoard.addPiece(move.getEndPosition(), new ChessPiece(movePiece.getTeamColor(), move.getPromotionPiece()));
            }
            //set next turn
            if (movePiece.getTeamColor() == TeamColor.WHITE) {
                teamTurn = TeamColor.BLACK;
            } else {
                teamTurn = TeamColor.WHITE;
            }
            setCastleFlags(startPosition);
            updateDoubleMovePawns(move, startPosition);
            updateFields();
        }
    }

    private void enPassantRemovePawn(ChessPiece movePiece, ChessPosition startPosition, ChessMove move) {
        //checks if an enPassant move has occurred. If so, remove the captured pawn from the board.
        ChessPiece.PieceType pieceType = movePiece.getPieceType();
        if (pieceType == ChessPiece.PieceType.PAWN) {
            if(doubleMovePawns.contains(new ChessPosition(startPosition.getRow(), move.getEndPosition().getColumn()))){
                chessBoard.addPiece(new ChessPosition(startPosition.getRow(), move.getEndPosition().getColumn()), null);
                doubleMovePawns.remove(new ChessPosition(startPosition.getRow(), move.getEndPosition().getColumn()));
            }
        }
    }

    private void updateDoubleMovePawns(ChessMove move, ChessPosition startPosition) {
        ChessPiece.PieceType endPieceType = chessBoard.getPiece(move.getEndPosition()).getPieceType();
        for (int i = 1; i < 9; i++) {
            if (isDoubleMove(move, startPosition, endPieceType, i)) {
                doubleMovePawns.add(move.getEndPosition());
            } else if (movedOutOfEnPassant(move, startPosition, i)) {
                doubleMovePawns.remove(startPosition);
            }
        }
        //make pawn no longer available for EnPassant if opponent doesn't remove pawn their next turn
        doubleMovePawns.removeIf(doublePawnPosition -> chessBoard.getPiece(doublePawnPosition).getTeamColor() == teamTurn);
    }

    private boolean movedOutOfEnPassant(ChessMove move, ChessPosition startPosition, int i) {
        ChessPiece.PieceType endPieceType = chessBoard.getPiece(move.getEndPosition()).getPieceType();
        if(endPieceType != ChessPiece.PieceType.PAWN){
            return false;
        }
        if(startPosition.equals(new ChessPosition(4, i))){
            return true;
        }
        else {
            return startPosition.equals(new ChessPosition(5, i));
        }
    }

    private static boolean isDoubleMove(ChessMove move, ChessPosition startPosition, ChessPiece.PieceType endPieceType, int i) {
        if(endPieceType != ChessPiece.PieceType.PAWN){
            return false;
        }
        if(startPosition.equals(new ChessPosition(2, i)) && move.getEndPosition().equals(new ChessPosition(4, i))){
            return true;
        }
        else {
            return startPosition.equals(new ChessPosition(7, i)) && move.getEndPosition().equals(new ChessPosition(5, i));
        }
    }

    private void setCastleFlags(ChessPosition startPosition) {
        if (startPosition.equals(new ChessPosition(1, 5))) {
            rwCastlePossible = false;
            lwCastlePossible = false;
        } else if (startPosition.equals(new ChessPosition(8, 5))) {
            rbCastlePossible = false;
            rwCastlePossible = false;
        } else if (startPosition.equals(new ChessPosition(1, 8))) {
            rwCastlePossible = false;
        } else if (startPosition.equals(new ChessPosition(1, 1))) {
            lwCastlePossible = false;
        } else if (startPosition.equals(new ChessPosition(8, 1))) {
            lbCastlePossible = false;
        } else if (startPosition.equals(new ChessPosition(8, 8))) {
            rbCastlePossible = false;
        }
    }

    public boolean isInCheck(TeamColor teamColor) {
        if (teamColor == TeamColor.WHITE) {
            return whiteCheck;
        } else {
            return blackCheck;
        }
    }

    public boolean isInCheckmate(TeamColor teamColor) {
        if (teamColor == TeamColor.WHITE) {
            return whiteCheckmate;
        } else {
            return blackCheckmate;
        }
    }

    public boolean isInStalemate(TeamColor teamColor) {
        if (teamColor == TeamColor.WHITE) {
            return whiteStalemate;
        } else {
            return blackStalemate;
        }
    }

    private void resetCastleFlags() {
        lwCastlePossible = true;
        rwCastlePossible = true;
        lbCastlePossible = true;
        rbCastlePossible = true;
    }

    public ChessBoard getBoard() {
        return chessBoard;
    }

    public void setBoard(ChessBoard board) {
        chessBoard = board;
        updateFields();
        resetCastleFlags();
        this.doubleMovePawns = new ArrayList<>();
    }

    public enum TeamColor {
        WHITE, BLACK
    }
}
