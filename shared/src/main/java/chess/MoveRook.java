package chess;

import java.util.Collection;

public class MoveRook extends MovePiece {

    public MoveRook(ChessBoard board, ChessPosition myPosition) {
            super(board, myPosition);
    }

    public Collection<ChessMove> pieceMove() {
        sweepDirections(new int[][]{{1,0},{-1,0},{0,1},{0,-1}});
            return moves;
    }
}
