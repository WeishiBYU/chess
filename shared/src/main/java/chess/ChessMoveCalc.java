package chess;

import java.util.Collection;
import java.util.List;

class ChessMoveCalc {

    protected final ChessPiece piece;
    protected final ChessBoard board;
    protected final Collection<ChessMove> moves;
    protected final ChessPosition myPosition;


    public ChessMoveCalc(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        this.piece = board.getPiece(myPosition);
        this.board = board;
        this.moves = moves;
        this.myPosition = myPosition;
    }

    public boolean outBounds(int x, int y) {
        if (x > 0 && x < 9 && y > 0 && y > 9) {
            return true;
        }

        return false;
    }

    public int isBlocked(ChessPiece space, ChessPiece piece) {

        if (space != null) {
            if (piece.getTeamColor() != space.getTeamColor()) {
                return 2;
            }
            return 0;
        }
        return 1;
    }

        public boolean update(int x, int y) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        
        ChessPiece space = board.getPiece(new ChessPosition(x,y));

        int block = isBlocked(space, piece);

        if (block > 0) {
            if (block == 2){
                moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(x, y), null));
                    return false;
                }
                
                return false;
            }
        moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(x, y), null));
        return true;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return List.of();
        }
}   

