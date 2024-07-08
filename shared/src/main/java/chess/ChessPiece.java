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

    @Override
    public String toString() {
        if (pieceColor == ChessGame.TeamColor.BLACK) {
          if (type == ChessPiece.PieceType.KING) {
              return "k";
          }
          else if (type == ChessPiece.PieceType.QUEEN) {
              return "q";
          }
          else if (type == ChessPiece.PieceType.KNIGHT) {
              return "n";
          }
          else if (type == ChessPiece.PieceType.BISHOP) {
              return "b";
          }
          else if (type == ChessPiece.PieceType.ROOK) {
              return "r";
          }
          else if (type == ChessPiece.PieceType.PAWN) {
              return "p";
          }
        }
        else{
            if (type == ChessPiece.PieceType.KING) {
                return "K";
            }
            else if (type == ChessPiece.PieceType.QUEEN) {
                return "Q";
            }
            else if (type == ChessPiece.PieceType.KNIGHT) {
                return "N";
            }
            else if (type == ChessPiece.PieceType.BISHOP) {
                return "B";
            }
            else if (type == ChessPiece.PieceType.ROOK) {
                return "R";
            }
            else if (type == ChessPiece.PieceType.PAWN) {
                return "P";
            }
        }
        return null;
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

        if(type == ChessPiece.PieceType.BISHOP) {
            return CalculateBishopMoves(board, myPosition);
        }
        else if(type == ChessPiece.PieceType.KING) {
            return CalculateKingMoves(board, myPosition);
        }
        else if(type == ChessPiece.PieceType.KNIGHT) {
            return CalculateKnightMoves(board, myPosition);
        }
        else if(type == ChessPiece.PieceType.PAWN) {
            return CalculatePawnMoves(board, myPosition);
        }
        else if(type == ChessPiece.PieceType.QUEEN) {
            return CalculateQueenMoves(board, myPosition);
        }
        else if(type == ChessPiece.PieceType.ROOK) {
            Collection<ChessMove> moves =  new ArrayList<>();
            return CalculateRookMoves(board, myPosition, moves);
        }
        return new ArrayList<>();
    }
    public Collection<ChessMove> CalculateRookMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        for (int i = 1; i < 8; i++) {
            if (!MoveHelper(board, myPosition, moves, row + i, col, null)) {
                break;
            }
        }
        for (int i = 1; i < 8; i++) {
            if (!MoveHelper(board, myPosition, moves, row - i, col, null)) {
                break;
            }
        }
        for (int i = 1; i < 8; i++) {
            if (!MoveHelper(board, myPosition, moves, row, col - i, null)) {
                break;
            }
        }
        for (int i = 1; i < 8; i++) {
            if (!MoveHelper(board, myPosition, moves, row, col + i, null)) {
                break;
            }
        }
        return moves;
    }
    public Collection<ChessMove> CalculateQueenMoves(ChessBoard board, ChessPosition myPosition) {
        //Calculates possible Queen Moves by combining Rook and Bishop logic.
        Collection<ChessMove> moves = CalculateBishopMoves(board, myPosition);
        return CalculateRookMoves(board, myPosition, moves);
    }

    public boolean PawnHelper(ChessBoard board, ChessPosition myPosition, int x, int y, ChessPiece.PieceType promotionPiece, Collection<ChessMove> moves) {
       //Handles additional functions for calculating pawn moves such as promotion and possible captures
        if(x<=8 && x>=1 && y<=8 && y>=1){
            ChessPosition desiredPosition = new ChessPosition(x, y);
            ChessPosition frontLeft = new ChessPosition(x, y-1);
            ChessPosition frontRight = new ChessPosition(x, y+1);
            if(y-1>0 && board.getPiece(frontLeft)!=null) {
                if(board.getPiece(frontLeft).getTeamColor()!=pieceColor) {
                    moves.add(new chess.ChessMove(myPosition, frontLeft, promotionPiece));
                }
            }
            if(y+1 < 8 && board.getPiece(frontRight)!=null) {
                if(board.getPiece(frontRight).getTeamColor()!=pieceColor) {
                    moves.add(new chess.ChessMove(myPosition, frontRight, promotionPiece));
                }
            }
            if(board.getPiece(desiredPosition)==null) {
                moves.add(new chess.ChessMove(myPosition, desiredPosition, promotionPiece));
                return true;
            }
        }
        return false;
    }
    public Collection<ChessMove> CalculatePawnMoves(ChessBoard board, ChessPosition myPosition) {
        //Uses PawnHelper function to help calculate possible moves in all scenarios for pawns.
        //PawnHelper also handles promotions.
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        //These two are cases for non-promotion and non-starting pawn moves
        if(pieceColor == ChessGame.TeamColor.WHITE && row>2 && row < 7)  {
            PawnHelper(board, myPosition, row+1, col, null, moves);
        }
        else if(pieceColor == ChessGame.TeamColor.BLACK && row < 7 && row > 2) {
            PawnHelper(board, myPosition, row-1, col, null, moves);
        }
        //Cases for black/white starting moves
        else if(pieceColor == ChessGame.TeamColor.WHITE && row == 2) {
            if(PawnHelper(board, myPosition, row+1, col, null, moves)){
                PawnHelper(board, myPosition, row+2, col, null, moves);
            }
        }
        else if(pieceColor == ChessGame.TeamColor.BLACK && row == 7) {
            if(PawnHelper(board, myPosition, row-1, col, null, moves)) {
                PawnHelper(board, myPosition, row - 2, col, null, moves);
            }
        }
        //Cases for black/white promotion
        if(pieceColor == ChessGame.TeamColor.WHITE && row == 7)  {
            PawnHelper(board, myPosition, row+1, col, PieceType.QUEEN, moves);
            PawnHelper(board, myPosition, row+1, col, PieceType.KNIGHT, moves);
            PawnHelper(board, myPosition, row+1, col, PieceType.ROOK, moves);
            PawnHelper(board, myPosition, row+1, col, PieceType.BISHOP, moves);
        }
        else if(pieceColor == ChessGame.TeamColor.BLACK && row == 2) {
            PawnHelper(board, myPosition, row-1, col, PieceType.QUEEN, moves);
            PawnHelper(board, myPosition, row-1, col, PieceType.KNIGHT, moves);
            PawnHelper(board, myPosition, row-1, col, PieceType.ROOK, moves);
            PawnHelper(board, myPosition, row-1, col, PieceType.BISHOP, moves);
        }
        return moves;
    }
    public Collection<ChessMove> CalculateKnightMoves(ChessBoard board, ChessPosition myPosition) {
        //Uses MoveHelper function to check all squares next to King to see if moves are available
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        MoveHelper(board, myPosition, moves, row+2, col+1, null);
        MoveHelper(board, myPosition, moves, row+2, col-1, null);
        MoveHelper(board, myPosition, moves, row+1, col-2, null);
        MoveHelper(board, myPosition, moves, row+1, col+2, null);
        MoveHelper(board, myPosition, moves, row-1, col-2, null);
        MoveHelper(board, myPosition, moves, row-1, col+2, null);
        MoveHelper(board, myPosition, moves, row-2, col+1, null);
        MoveHelper(board, myPosition, moves, row-2, col-1, null);
        return moves;
    }
    public Collection<ChessMove> CalculateKingMoves(ChessBoard board, ChessPosition myPosition) {
        //Uses MoveHelper function to check all squares next to King to see if moves are available
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        MoveHelper(board, myPosition, moves, row+1, col, null);
        MoveHelper(board, myPosition, moves, row+1, col+1, null);
        MoveHelper(board, myPosition, moves, row+1, col-1, null);
        MoveHelper(board, myPosition, moves, row, col-1, null);
        MoveHelper(board, myPosition, moves, row, col+1, null);
        MoveHelper(board, myPosition, moves, row-1, col, null);
        MoveHelper(board, myPosition, moves, row-1, col+1, null);
        MoveHelper(board, myPosition, moves, row-1, col-1, null);
        return moves;
    }
    public boolean MoveHelper(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves, int x, int y, ChessPiece.PieceType promotionPiece) {
        //Checks if desired move is out of bounds or if piece is already located at desired position
        if(x<=8 && x>=1 && y<=8 && y>=1){
            ChessPosition desiredPosition = new ChessPosition(x, y);
            if(board.getPiece(desiredPosition)==null) {
                moves.add(new chess.ChessMove(myPosition, desiredPosition, promotionPiece));
                return true;
            }
            else {
                if(board.getPiece(desiredPosition).getTeamColor()!=pieceColor) {
                    moves.add(new chess.ChessMove(myPosition, desiredPosition, promotionPiece));
                }
                return false;
            }
        }
        return false;
    }
    public Collection<ChessMove> CalculateBishopMoves(ChessBoard board, ChessPosition myPosition) {
        //Calculates all possible diagonal moves for bishop using MoveHelper function.
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        for(int i = 1; i < 8; i++) {
            if(!MoveHelper(board, myPosition, moves, row+i, col+i, null)) {
                break;
            }
        }
        for(int i = 1; i < 8; i++) {
            if(!MoveHelper(board, myPosition, moves, row-i, col+i, null)) {
                break;
            }
        }
        for(int i = 1; i < 8; i++) {
            if(!MoveHelper(board, myPosition, moves, row+i, col-i, null)) {
                break;
            }
        }
        for(int i = 1; i < 8; i++) {
            if(!MoveHelper(board, myPosition, moves, row-i, col-i, null)) {
                break;
            }
        }
        return moves;
    }
}
