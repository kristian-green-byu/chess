package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        //Check if piece is Bishop. If so, calculate moves based on bishop.

        if(type == ChessPiece.PieceType.BISHOP) {
            return CalculateBishopMoves(board, myPosition);
        }
        else if(type == ChessPiece.PieceType.KING) {
            return CalculateKingMoves(board, myPosition);
        }
        return new ArrayList<>();
    }
    public Collection<ChessMove> CalculateKingMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        MoveHelper(board, myPosition, moves, row+1, col);
        MoveHelper(board, myPosition, moves, row+1, col+1);
        MoveHelper(board, myPosition, moves, row+1, col-1);
        MoveHelper(board, myPosition, moves, row, col-1);
        MoveHelper(board, myPosition, moves, row, col+1);
        MoveHelper(board, myPosition, moves, row-1, col);
        MoveHelper(board, myPosition, moves, row-1, col+1);
        MoveHelper(board, myPosition, moves, row-1, col-1);
        return moves;
    }
    public boolean MoveHelper(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves, int x, int y) {
        if(x<=8 && x>=1 && y<=8 && y>=1){
            ChessPosition desiredPosition = new ChessPosition(x, y);
            if(board.getPiece(desiredPosition)==null) {
                moves.add(new chess.ChessMove(myPosition, desiredPosition, null));
                return true;
            }
            else {
                if(board.getPiece(desiredPosition).getTeamColor()!=pieceColor) {
                    moves.add(new chess.ChessMove(myPosition, desiredPosition, null));
                }
                return false;
            }
        }
        return false;
    }
    public Collection<ChessMove> CalculateBishopMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        for(int i = 1; i < 8; i++) {
            if(!MoveHelper(board, myPosition, moves, row+i, col+i)) {
                break;
            }
        }
        for(int i = 1; i < 8; i++) {
            if(!MoveHelper(board, myPosition, moves, row-i, col+i)) {
                break;
            }
        }
        for(int i = 1; i < 8; i++) {
            if(!MoveHelper(board, myPosition, moves, row+i, col-i)) {
                break;
            }
        }
        for(int i = 1; i < 8; i++) {
            if(!MoveHelper(board, myPosition, moves, row-i, col-i)) {
                break;
            }
        }
        return moves;
    }
}
