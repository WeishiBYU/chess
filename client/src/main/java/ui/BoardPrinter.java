package ui;
import static ui.EscapeSequences.*;

import java.util.List;
import java.util.Collection;

import chess.*;


public class BoardPrinter {
    ChessBoard gameBoard = new ChessBoard();
    List<String> letters = List.of("   ", " a ", " b ", " c ", " d " , " e ", " f ", " g ", " h ", "   ");
    List<String> numbers = List.of("   ", " 1 ", " 2 ", " 3 ", " 4 " , " 5 ", " 6 ", " 7 ", " 8 ", "   ");



    public void drawBoard(ChessBoard board, boolean isWhitePerspective) {
        gameBoard = nullBoard(board) ? gameBoard : board;

        for (int r = 0; r < 8; r++) {
            System.out.print(SET_BG_COLOR_BLUE);

            System.out.print(letters.get(r));
        }   

        // 2. Nested Loop for the board
        for (int r = 0; r < 9; r++) {
            int row = isWhitePerspective ? (8 - r) : r;
            
            System.out.print(SET_BG_COLOR_BLUE);

            System.out.print(numbers.get(row));

            for (int c = 0; c < 9; c++) {
                int col = isWhitePerspective ? c : (8 - c);

                // Set Background Color (Alternating)
                if ((row + col) % 2 == 0) {
                    System.out.print(SET_BG_COLOR_DARK_GREY);

                } else {
                    System.out.print(SET_BG_COLOR_LIGHT_GREY);
                }
                
                ChessPiece piece = gameBoard.getPiece(new ChessPosition(row + 1, col + 1));
                if (piece != null) {
                    var color = piece.getTeamColor(); // or use to choose symbol
                    // print the piece symbol here
                } else {
                    System.out.print(EMPTY);
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
