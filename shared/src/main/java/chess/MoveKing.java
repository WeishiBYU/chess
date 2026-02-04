package chess;

import java.util.Collection;

public class MoveKing extends MovePiece {

    public MoveKing(ChessBoard board, ChessPosition myPosition) {
            super(board, myPosition);
    }

    public Collection<ChessMove> pieceMove() {
        int x = myPosition.getRow();
        int y = myPosition.getColumn();

        update(x+1,y+1);
        update(x-1,y-1);
        update(x-1,y+1);
        update(x+1,y);
        update(x+1,y-1);
        update(x-1,y);
        update(x,y-1);
        update(x,y+1);

        return moves;
    }
}
