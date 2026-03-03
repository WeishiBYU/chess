package chess;

import java.util.Collection;

public class MoveBishop extends MovePiece {

    public MoveBishop(ChessBoard board, ChessPosition myPosition) {
            super(board, myPosition);
    }

    public Collection<ChessMove> pieceMove() {
        sweepDirections(new int[][]{{1,1},{-1,1},{-1,-1},{1,-1}});
        return moves;
    }
}
