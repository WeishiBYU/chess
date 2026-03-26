package ui;
import static ui.EscapeSequences.*;

import java.util.List;
import java.util.Collection;

import chess.*;


public class BoardPrinter {
    ChessBoard gameBoard = new ChessBoard();
    List<String> letters = List.of("   ", " a ", " b ", " c ", " d " , " e ", " f ", " g ", " h ", "   ");
    List<String> numbers = List.of("   ", " 1 ", " 2 ", " 3 ", " 4 " , " 5 ", " 6 ", " 7 ", " 8 ", "   ");

    public BoardPrinter() {
        gameBoard.resetBoard();
    }
    
    public void drawBoard(ChessBoard board, boolean isWhitePerspective) {
        gameBoard = nullBoard(board) ? gameBoard : board;

        for (int r = 0; r < 8; r++) {
            System.out.print(SET_BG_COLOR_BLUE);

            System.out.print(letters.get(r + 1));
        }   

        // 2. Nested Loop for the board
        for (int r = 0; r < 8; r++) {
            int row = isWhitePerspective ? (7 - r) : r;
            
            System.out.print(SET_BG_COLOR_BLUE);

            System.out.print(numbers.get(row + 1));

            for (int c = 0; c < 8; c++) {
                int col = isWhitePerspective ? c : (7 - c);

                // Set Background Color (Alternating)
                if ((row + col) % 2 == 0) {
                    System.out.print(SET_BG_COLOR_DARK_GREY);

                } else {
                    System.out.print(SET_BG_COLOR_LIGHT_GREY);
                }
                
                ChessPiece piece = gameBoard.getPiece(new ChessPosition(row + 1, col + 1));
                
                var symbol = EMPTY;

                if (piece != null) {
                    boolean white = piece.getTeamColor() == ChessGame.TeamColor.WHITE;


                    symbol = switch (piece.getPieceType()) {
                        case KING   -> white ? WHITE_KING   : BLACK_KING;
                        case QUEEN  -> white ? WHITE_QUEEN  : BLACK_QUEEN;
                        case BISHOP -> white ? WHITE_BISHOP : BLACK_BISHOP;
                        case KNIGHT -> white ? WHITE_KNIGHT : BLACK_KNIGHT;
                        case ROOK   -> white ? WHITE_ROOK   : BLACK_ROOK;
                        case PAWN   -> white ? WHITE_PAWN   : BLACK_PAWN;
                    };

                    System.out.print(symbol);
                }
            }
            System.out.println(RESET_BG_COLOR); // End of row
        }
    }

    private boolean nullBoard(ChessBoard board) {
        if (board == null) {
            return true;
        }

        return false;
    }
}
