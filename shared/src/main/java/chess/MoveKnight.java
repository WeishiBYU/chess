package chess;

import java.util.Collection;

public class MoveKnight extends MovePiece {

    public MoveKnight(ChessBoard board, ChessPosition myPosition) {
            super(board, myPosition);
    }

    public Collection<ChessMove> pieceMove() {
        int x = myPosition.getRow();
        int y = myPosition.getColumn();

        update(x+1,y+2);
        update(x-1,y-2);
        update(x-2,y+1);
        update(x+2,y-1);
        update(x+1,y-2);
        update(x-1,y+2);
        update(x-2,y-1);
        update(x+2,y+1);

        return moves;
    }
}
