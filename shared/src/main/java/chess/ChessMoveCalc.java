package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Collection;

class ChessMoveCalc {

    protected final ChessPiece piece;
    protected final ChessBoard board;
    protected final Collection<ChessMove> moves;
    protected final ChessPosition myPosition;


    public ChessMoveCalc(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        this.piece = board.getPiece(myPosition);
        this.board = board;
        this.moves = new ArrayList<>();
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
                moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(x, y), null));
                return true;
            }
        return false;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return List.of();
        }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((piece == null) ? 0 : piece.hashCode());
        result = prime * result + ((board == null) ? 0 : board.hashCode());
        result = prime * result + ((moves == null) ? 0 : moves.hashCode());
        result = prime * result + ((myPosition == null) ? 0 : myPosition.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ChessMoveCalc other = (ChessMoveCalc) obj;
        if (piece == null) {
            if (other.piece != null)
                return false;
        } else if (!piece.equals(other.piece))
            return false;
        if (board == null) {
            if (other.board != null)
                return false;
        } else if (!board.equals(other.board))
            return false;
        if (moves == null) {
            if (other.moves != null)
                return false;
        } else if (!moves.equals(other.moves))
            return false;
        if (myPosition == null) {
            if (other.myPosition != null)
                return false;
        } else if (!myPosition.equals(other.myPosition))
            return false;
        return true;
    }

    
}   

