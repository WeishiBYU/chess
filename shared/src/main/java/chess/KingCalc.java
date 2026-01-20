package chess;

import java.util.Collection;
import java.util.List;

public class KingCalc extends ChessMoveCalc {

    public KingCalc(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        super(board, myPosition, moves);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
               
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        update(row, col-1);
        update(row, col+1);
        update(row-1, col);
        update(row+1, col);
        update(row-1, col-1);
        update(row+1, col+1);
        update(row-1, col+1);
        update(row+1, col-1);

        return moves;
        }

}