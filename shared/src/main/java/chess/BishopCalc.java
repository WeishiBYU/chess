package chess;

import java.util.Collection;
import java.util.List;

public class BishopCalc extends ChessMoveCalc {

    public BishopCalc(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        super(board, myPosition, moves);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
               
        int row = myPosition.getRow();
        int col = myPosition.getColumn();


        for (int x = row-1, y = col-1; x > 0 && y > 0; x--, y--) {
            if (!update(x,y)) break;
        }

        for (int x = row+1, y = col-1; x <= 8 && y > 0; x++, y--) {
            if (!update(x,y)) break;

        }

        for (int x = row-1, y = col+1; x > 0 && y <= 8; x--, y++) {
            if (!update(x,y)) break;
        }

        for (int x = row+1, y = col+1; x <= 8 && y <= 8; x++, y++) {
            if (!update(x,y)) break;
        }

        return moves;
        }

}