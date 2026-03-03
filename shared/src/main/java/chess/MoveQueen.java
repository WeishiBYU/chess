package chess;

import java.util.Collection;

public class MoveQueen extends MovePiece {

    public MoveQueen(ChessBoard board, ChessPosition myPosition) {
            super(board, myPosition);
    }

    public Collection<ChessMove> pieceMove() {
        sweepDirections(new int[][]{
            {1,0},{-1,0},{0,1},{0,-1},
            {1,1},{-1,1},{-1,-1},{1,-1}
        });
        return moves;
    }
}
