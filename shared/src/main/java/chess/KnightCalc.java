package chess;

import java.util.Collection;
import java.util.List;

public class KnightCalc extends ChessMoveCalc {

    public KnightCalc(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        super(board, myPosition, moves);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
               
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        update(row+2, col-1);
        update(row+2, col+1);
        update(row-1, col+2);
        update(row+1, col+2);
        update(row-1, col-2);
        update(row+1, col-2);
        update(row-2, col+1);
        update(row-2, col-1);

        return moves;
        }

}