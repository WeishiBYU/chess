package chess;

import java.util.Collection;
import java.util.List;

public class RookCalc extends ChessMoveCalc {

    public RookCalc(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        super(board, myPosition, moves);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
               
        int row = myPosition.getRow();
        int col = myPosition.getColumn();


        for (int x = row-1, y = col; x > 0; x--) {
            if (!update(x,y)) break;
        }

        for (int x = row+1, y = col; x <= 8; x++) {
            if (!update(x,y)) break;

        }

        for (int x = row, y = col-1; y > 0; y--) {
            if (!update(x,y)) break;
        }

        for (int x = row, y = col+1; y <= 8; y++) {
            if (!update(x,y)) break;
        }

        return moves;
        }

}