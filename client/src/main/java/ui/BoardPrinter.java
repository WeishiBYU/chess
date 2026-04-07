package ui;
import static ui.EscapeSequences.*;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;


public class BoardPrinter {
    List<String> letters = List.of("   ", " a ", " b ", " c ", " d " , " e ", " f ", " g ", " h ", "   ");
    List<String> numbers = List.of(" 1 ", " 2 ", " 3 ", " 4 " , " 5 ", " 6 ", " 7 ", " 8 ");


    public void drawBoard(ChessGame game, String color) {
        drawBoardHelper(game, color, null, Collections.emptySet());
    }

    public void drawMoves(ChessGame game, String color, Collection<ChessMove> moves) {
        if (moves == null || moves.isEmpty()) {
            drawBoard(game, color);
            return;
        }
        
        Set<ChessPosition> endPositions = new HashSet<>();
        for (ChessMove move : moves) {
            endPositions.add(move.getEndPosition());
        }
        ChessPosition startPosition = moves.iterator().next().getStartPosition();
        
        drawBoardHelper(game, color, startPosition, endPositions);
    }


    private void drawBoardHelper(ChessGame game, String color, ChessPosition startPosition, Set<ChessPosition> endPositions) {
        ChessBoard board = game.getBoard();
        System.out.print("\n");

        drawHeader(color);

        for (int r = 0; r < 8; r++) {
            int row = isWhite(color) ? (7 - r) : r;
            System.out.print(SET_BG_COLOR_BLUE + numbers.get(row)); // Left number

            for (int c = 0; c < 8; c++) {
                int col = isWhite(color) ? c : (7 - c);
                ChessPosition currentPos = new ChessPosition(row + 1, col + 1);

                setBackgroundColor(currentPos, startPosition, endPositions, (row + col) % 2 == 0);
                
                printPiece(board, currentPos);
            }
            System.out.print(SET_BG_COLOR_BLUE + numbers.get(row)); // Right number
            System.out.println(RESET_BG_COLOR);
        }

        // Draw bottom header
        drawHeader(color);
        System.out.print(RESET_BG_COLOR);
    }

    private void drawHeader(String color) {
        for (int c = 0; c < 10; c++) {
            int perspective = isWhite(color) ? c : (letters.size() - 1 - c);
            System.out.print(SET_BG_COLOR_BLUE + letters.get(perspective));
        }
        System.out.println(RESET_BG_COLOR);
    }

    private void setBackgroundColor(ChessPosition currentPos, ChessPosition startPos, Set<ChessPosition> endPositions, boolean isDarkSquare) {
        if (currentPos.equals(startPos)) {
            System.out.print(SET_BG_COLOR_YELLOW);
        } else if (endPositions.contains(currentPos)) {
            System.out.print(isDarkSquare ? SET_BG_COLOR_DARK_GREEN : SET_BG_COLOR_GREEN);
        } else {
            System.out.print(isDarkSquare ? SET_BG_COLOR_DARK_GREY : SET_BG_COLOR_LIGHT_GREY);
        }
    }

    private void printPiece(ChessBoard board, ChessPosition position) {
        ChessPiece piece = board.getPiece(position);
        var symbol = "   ";
        if (piece != null) {
            boolean white = piece.getTeamColor() == ChessGame.TeamColor.WHITE;
            symbol = switch (piece.getPieceType()) {
                case KING   -> white ? " K " : " k ";
                case QUEEN  -> white ? " Q " : " q ";
                case BISHOP -> white ? " B " : " b ";
                case KNIGHT -> white ? " N " : " n ";
                case ROOK   -> white ? " R " : " r ";
                case PAWN   -> white ? " P " : " p ";
            };
        }
        System.out.print(symbol);
    }

    private boolean isWhite(String color) {
        return color == null || color.equalsIgnoreCase("white");
    }
}