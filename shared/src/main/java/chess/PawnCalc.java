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

    private void proms(int row,int col, int x, int y) {
        ChessPosition from = new ChessPosition(row, col);
        ChessPosition to = new ChessPosition(x, y);

        moves.add(new ChessMove(from, to, ChessPiece.PieceType.QUEEN));
        moves.add(new ChessMove(from, to, ChessPiece.PieceType.BISHOP));
        moves.add(new ChessMove(from, to, ChessPiece.PieceType.ROOK));
        moves.add(new ChessMove(from, to, ChessPiece.PieceType.KNIGHT));

        
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
               
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        int color = colorpicker();
        int newRow = row+color;

        ChessPiece left;
        ChessPiece right;

        if (outBounds(newRow, col-1)) {
            left = board.getPiece(new ChessPosition(newRow, col-1));

            if (isBlocked(left, piece) == 2) {
                if (newRow == 1 || newRow == 8) proms(row,col,row+color, col-1);
                else update(newRow, col-1);
            }
        }

        if (outBounds(row+color, col+1)) {
            right = board.getPiece(new ChessPosition(newRow, col+1));

            if (isBlocked(right, piece) == 2) {
                if (newRow == 1 || newRow == 8) proms(row,col,row+color, col+1);
                else update(newRow, col+1);
            }
        }
            
        if (newRow == 1 || newRow == 8) proms(row,col,row+color, col);
        else update(newRow, col);

        if (color == 1 && row == 2) {
            update(row+color*2, col);
        }
        else if (color == -1 && row == 7) {
            update(row+color*2, col);
        }

        return moves;
        }

}