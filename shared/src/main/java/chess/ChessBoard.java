package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece[][] tiles = new ChessPiece[8][8];

    public ChessBoard() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(tiles, that.tiles);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(tiles);
    }

    @Override
    public String toString() {
        StringBuilder chessBoardString = new StringBuilder();
        for (int i = 7; i >= 0; i--) {
            for (int j = 0; j < 8; j++) {
                if (tiles[i][j] != null) {
                    chessBoardString.append('|').append(tiles[i][j].toString());
                } else {
                    chessBoardString.append("| ");
                }
            }
            chessBoardString.append('|').append("\n");
        }
        return chessBoardString.toString();
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        tiles[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return tiles[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        var board = new ChessBoard();
        //Add in black and white Pawns
        for (int i = 1; i <= 8; i++) {
            board.addPiece(new ChessPosition(2, i), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
            board.addPiece(new ChessPosition(7, i), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        }
        //Add in black and white Rooks
        for (int i = 1; i <= 8; i += 7) {
            board.addPiece(new ChessPosition(1, i), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
            board.addPiece(new ChessPosition(8, i), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        }
        //Add in Knights
        for (int i = 2; i <= 7; i += 5) {
            board.addPiece(new ChessPosition(1, i), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
            board.addPiece(new ChessPosition(8, i), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        }
        //Add in Bishops
        for (int i = 3; i <= 6; i += 3) {
            board.addPiece(new ChessPosition(1, i), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
            board.addPiece(new ChessPosition(8, i), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        }
        //Add in Queens
        board.addPiece(new ChessPosition(1, 4), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
        board.addPiece(new ChessPosition(8, 4), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));
        //Add in Kings
        board.addPiece(new ChessPosition(1, 5), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
        board.addPiece(new ChessPosition(8, 5), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
        this.tiles = board.tiles;
    }
}
