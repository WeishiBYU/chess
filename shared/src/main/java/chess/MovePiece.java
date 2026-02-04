package chess;

import java.util.Collection;
import java.util.ArrayList;

public class MovePiece {
    protected final ChessGame.TeamColor color;
    protected final ChessPosition myPosition;
    protected final ChessPiece piece;
    protected final ChessBoard board;
    protected final Collection<ChessMove> moves;

    public MovePiece(ChessBoard board, ChessPosition myPosition) {
        this.piece = board.getPiece(myPosition);
        this.board = board;
        this.myPosition = myPosition;
        this.color = piece.getTeamColor();
        this.moves = new ArrayList<>();
    }

    public boolean outBound(int x, int y) {
        if (x > 0 && x < 9 && y > 0 && y < 9) return false;

        return true;
    }

    public int blocked(ChessPosition pos) {
        ChessPiece space = board.getPiece(pos);
        
        if (space != null) {
            if (color != space.getTeamColor()) return 2;
            
            return 0;
        }
        return 1;
    }

    public boolean update(int x, int y) {
        if (outBound(x,y)) return false;
        ChessPosition pos = new ChessPosition(x,y);

        if (blocked(pos) > 0) {
            moves.add(new ChessMove(myPosition, pos, null));
            
            if (blocked(pos) == 2) return false;

            return true;
        }

        return false;
    }
}
