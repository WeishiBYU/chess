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

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);

        if (piece.type == PieceType.BISHOP) {

            MoveBishop pc = new MoveBishop(board, myPosition);

            return pc.pieceMove();
        }

        else if (piece.type == PieceType.ROOK) {

            MoveRook pc = new MoveRook(board, myPosition);

            return pc.pieceMove();
        }

        else if (piece.type == PieceType.QUEEN) {

            MoveQueen pc = new MoveQueen(board, myPosition);

            return pc.pieceMove();
        }

        else if (piece.type == PieceType.KING) {

            MoveKing pc = new MoveKing(board, myPosition);

            return pc.pieceMove();
        }

        else if (piece.type == PieceType.KNIGHT) {

            MoveKnight pc = new MoveKnight(board, myPosition);

            return pc.pieceMove();
        }

        else if (piece.type == PieceType.PAWN) {

            MovePawn pc = new MovePawn(board, myPosition);

            return pc.pieceMove();
        }

        return List.of();
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

    @Override
    public String toString() {
        return "ChessPiece [pieceColor=" + pieceColor + ", type=" + type + "]";
    }
}
