package chess;

import java.util.Collection;


public class PawnCalc extends ChessMoveCalc {

    public PawnCalc(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        super(board, myPosition, moves);
    }

    private int colorpicker() {
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            return 1;
        }
        return -1;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
               
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        int color = colorpicker();

        ChessPiece left;
        ChessPiece right;

        if (outBounds(row+color, col-1)) {
            left = board.getPiece(new ChessPosition(row+color, col-1));

            if (isBlocked(left, piece) == 2) {
                update(row+color, col-1);
            }
        }

        if (outBounds(row+color, col+1)) {
            right = board.getPiece(new ChessPosition(row+color, col+1));

            if (isBlocked(right, piece) == 2) {
                update(row+color, col+1);
            }
        }
            
        update(row+color, col);
        
        if (color == 1 && row == 2) {
            update(row+color*2, col);
        }
        else if (color == -1 && row == 7) {
            update(row+color*2, col);
        }

        if (color == 1 && row == 8) {
            update(row+color, col);
            update(row+color, col);
            update(row+color, col);
            update(row+color, col);
        }
        else if (color == -1 && row == 1) {
            update(row+color, col);
            update(row+color, col);
            update(row+color, col);
            update(row+color, col);
        }

        return moves;
        }

}