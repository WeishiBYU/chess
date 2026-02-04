package chess;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    TeamColor color = TeamColor.WHITE;
    ChessBoard game = new ChessBoard();

    Collection<ChessPosition> white = new ArrayList<>();
    Collection<ChessMove> wMoves = new ArrayList<>();
    ChessPosition wKing;
    Collection<ChessPosition> black = new ArrayList<>();
    Collection<ChessMove> bMoves = new ArrayList<>();
    ChessPosition bKing;

    Collection<ChessMove> vMoves = new ArrayList<>();

    public ChessGame() {
    }
    
    public void cPieces() {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                ChessPosition pos = new ChessPosition(x+1, y+1);
                ChessPiece piece = game.getPiece(pos);

                if (piece != null && TeamColor.WHITE.equals(piece.getTeamColor())) {
                    if(piece.getPieceType() == ChessPiece.PieceType.KING) {
                        wKing = pos;
                    }
                    white.add(pos);
                }
                else if (piece != null && TeamColor.BLACK.equals(piece.getTeamColor())) {
                    if(piece.getPieceType() == ChessPiece.PieceType.KING) {
                        bKing = pos;
                    }
                    black.add(pos);
                }
            }
        }
    }

    public void cMoves(TeamColor teamColor) {
        cPieces();
        for(ChessPosition pos : white) {
            ChessPiece p = game.getPiece(pos);

            wMoves.addAll(p.pieceMoves(game, pos));
        }

        for(ChessPosition pos : black) {
            ChessPiece p = game.getPiece(pos);

            bMoves.addAll(p.pieceMoves(game, pos));
        }
    }   

    public Collection<ChessMove> moveCheck(Collection<ChessMove> moves) {
        Collection<ChessMove> valMoves = new ArrayList<>();

        for(ChessMove move: moves) {
            ChessBoard clone = game.makeCopy();
            ChessPiece piece = game.getPiece(move.getStartPosition());

            game.addPiece(move.getEndPosition(), piece);
            game.removePiece(move.getStartPosition());

            if (!isInCheck(color)) valMoves.add(move);

            setBoard(clone);
            //Make the move happen then check if it is in check then go back to the clone and if it was not in check add it to the v list.
        }
        return valMoves;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return color;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        cMoves(team);

        color = team;

        if (color.equals(TeamColor.WHITE)) {
            moveCheck(wMoves);
        }

        else moveCheck(bMoves);
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = game.getPiece(startPosition);

        return piece.pieceMoves(game, startPosition);
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (vMoves.contains(move)) {
            ChessPiece p = game.getPiece(move.getStartPosition());

            game.addPiece(move.getEndPosition(), p);
            game.removePiece(move.getStartPosition());
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        cMoves(teamColor);

        if (TeamColor.WHITE == teamColor) {
            for(ChessMove move : bMoves) {
                ChessPosition pos = move.getEndPosition();

                if (pos.equals(wKing)) {
                    return true;
                }
            }
            return false;
        }

        else {
            for(ChessMove move : wMoves) {
                ChessPosition pos = move.getEndPosition();

                if (pos.equals(bKing)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (vMoves.size() != 0) return false;
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (vMoves.size() != 0) return false;
        return true;    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        game = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return game;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((color == null) ? 0 : color.hashCode());
        result = prime * result + ((game == null) ? 0 : game.hashCode());
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
        ChessGame other = (ChessGame) obj;
        if (color != other.color)
            return false;
        if (game == null) {
            if (other.game != null)
                return false;
        } else if (!game.equals(other.game))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ChessGame [color=" + color + ", game=" + game + ", white=" + white + ", wMoves=" + wMoves + ", wKing="
                + wKing + ", black=" + black + ", bMoves=" + bMoves + ", bKing=" + bKing + ", vMoves=" + vMoves + "]";
    }

    
}
