package ui;
import static ui.EscapeSequences.RESET_BG_COLOR;
import static ui.EscapeSequences.SET_BG_COLOR_BLUE;
import static ui.EscapeSequences.SET_BG_COLOR_DARK_GREY;
import static ui.EscapeSequences.SET_BG_COLOR_LIGHT_GREY;

import java.util.List;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;


public class BoardPrinter {
    List<String> letters = List.of("   ", " a ", " b ", " c ", " d " , " e ", " f ", " g ", " h ", "   ");
    List<String> numbers = List.of(" 1 ", " 2 ", " 3 ", " 4 " , " 5 ", " 6 ", " 7 ", " 8 ");


    public void drawBoard(ChessGame game, String color) {
        ChessBoard board = game.getBoard();

        for (int c = 0; c < 10; c++) {

            int perspective = isWhite(color) ? c : (letters.size() - 1 - c);

            System.out.print(SET_BG_COLOR_BLUE);

            System.out.print(letters.get(perspective));
        }   

        System.out.println(RESET_BG_COLOR);

        for (int r = 0; r < 8; r++) {
            int row = isWhite(color) ? (7 - r) : r;

            System.out.print(SET_BG_COLOR_BLUE);

            System.out.print(numbers.get(row));

            for (int c = 0; c < 8; c++) {
                int col = isWhite(color) ? c : (7 - c);

                if ((row + col) % 2 == 0) {
                    System.out.print(SET_BG_COLOR_DARK_GREY);

                } else {
                    System.out.print(SET_BG_COLOR_LIGHT_GREY);
                }
                
                ChessPiece piece = board.getPiece(new ChessPosition(row + 1, col + 1));
                
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
            System.out.print(SET_BG_COLOR_BLUE);

            System.out.print(numbers.get(row));

            System.out.println(RESET_BG_COLOR);
        }

        for (int c = 0; c < 10; c++) {
            System.out.print(SET_BG_COLOR_BLUE);

            int perspective = isWhite(color) ? c : (letters.size() - 1 - c);
            
            System.out.print(letters.get(perspective));
        }   
        System.out.print(RESET_BG_COLOR);
    }

    

    private boolean isWhite(String color) {
        if (color.equals("white")) {
            return true;
        }

        return false;
    }
}
