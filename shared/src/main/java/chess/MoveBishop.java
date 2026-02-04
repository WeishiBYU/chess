package chess;

import java.util.Collection;

public class MoveBishop extends MovePiece {

    public MoveBishop(ChessBoard board, ChessPosition myPosition) {
            super(board, myPosition);
    }

    public Collection<ChessMove> pieceMove() {
        int x = myPosition.getRow();
        int y = myPosition.getColumn();

        for (int i = x+1, n = y+1; i > 0 && n > 0 && i < 9 && n < 9; i++, n++) {
            if (!update(i,n)) break;
            
        }

        for (int i = x-1, n = y+1; i > 0 && n > 0 && i < 9 && n < 9; i--, n++) {
            if (!update(i,n)) break;
        }

        for (int i = x-1, n = y-1; i > 0 && n > 0 && i < 9 && n < 9; i--, n--) {
            if (!update(i,n)) break;
        }

        for (int i = x+1, n = y-1; i > 0 && n > 0 && i < 9 && n < 9; i++, n--) {
            if (!update(i,n)) break;
        }

        return moves;
    }
}
