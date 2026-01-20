package chess;

import java.util.Collection;
import java.util.List;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChessPiece other)) {
            return false;
        }
        return pieceColor == other.pieceColor && type == other.type;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(pieceColor, type);
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        Collection<ChessMove> moves = List.of();

        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        if(piece.getPieceType() == PieceType.BISHOP) {
            BishopCalc bis = new BishopCalc(board, myPosition, moves);
            bis.pieceMoves(board, myPosition);
            return bis.moves;
        }
                
        if(piece.getPieceType() == PieceType.KNIGHT) {
            return List.of(new ChessMove(new ChessPosition(row, col), new ChessPosition(row-2, col+1), null),
                            new ChessMove(new ChessPosition(row, col), new ChessPosition(row-2, col-1), null),
                            new ChessMove(new ChessPosition(row, col), new ChessPosition(row+2, col-1), null),
                            new ChessMove(new ChessPosition(row, col), new ChessPosition(row+2, col+1), null),
                            new ChessMove(new ChessPosition(row, col), new ChessPosition(row-1, col+2), null),
                            new ChessMove(new ChessPosition(row, col), new ChessPosition(row-1, col-2), null),
                            new ChessMove(new ChessPosition(row, col), new ChessPosition(row+1, col-2), null),
                            new ChessMove(new ChessPosition(row, col), new ChessPosition(row+1, col+2), null));
        }

        return List.of();
    }

}
