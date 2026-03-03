package chess;

import java.util.Collection;

import chess.ChessGame.TeamColor;

public class MovePawn extends MovePiece {

    public MovePawn(ChessBoard board, ChessPosition myPosition) {
            super(board, myPosition);
    }

    private void promUpdate(ChessPosition pos) {

        if (outBound(pos.getRow(), pos.getColumn())) return;

        if (blocked(pos) > 0) {
            moves.add(new ChessMove(myPosition, pos, ChessPiece.PieceType.KNIGHT));
            moves.add(new ChessMove(myPosition, pos, ChessPiece.PieceType.QUEEN));
            moves.add(new ChessMove(myPosition, pos, ChessPiece.PieceType.BISHOP));
            moves.add(new ChessMove(myPosition, pos, ChessPiece.PieceType.ROOK));
        }
    }


    private void pawnProm(ChessPosition left, ChessPosition up, ChessPosition right) {

        if (blocked(up) == 1) {
            promUpdate(up);
        }
        
        if (blocked(left) == 2) {
            promUpdate(left);
        }

        if (blocked(right) == 2) {
            promUpdate(right);
        }
        
    }

    private void pawnMove(ChessPosition left, ChessPosition up, ChessPosition right) {
        if (blocked(up) == 1) {
            update(up.getRow(), up.getColumn());
        }
        
        if (blocked(left) == 2) {
            update(left.getRow(), left.getColumn());
        }

        if (blocked(right) == 2) {
            update(right.getRow(), right.getColumn());
        }
        
    }


    public Collection<ChessMove> pieceMove() {
        int x = myPosition.getRow();
        int y = myPosition.getColumn();
        int dir = 1;
        int start = 2;
        int prom = 7;
        
        ChessPosition up = myPosition;
        ChessPosition left = myPosition;
        ChessPosition right = myPosition;

        if (color == TeamColor.BLACK) {
            dir = -1;
            prom = 2;
            start = 7;
        }

        if (!outBound(x+dir,y)) {
            up = new ChessPosition(x+dir,y);
        }

        if (!outBound(x+dir,y-1)) {
            left = new ChessPosition(x+dir,y-1);
        }

        if (!outBound(x+dir,y+1)) {
            right = new ChessPosition(x+dir,y+1);
        }

        if (x == start && blocked(up) == 1 && blocked(new ChessPosition(x+dir+dir,y)) == 1) {
            update(x+dir+dir, y);
        }

        if (x == prom) pawnProm(left, up, right);

        else pawnMove(left, up, right);

        return moves;
    }
}
