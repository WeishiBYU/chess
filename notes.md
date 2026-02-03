These are the notes of my chess project!

- Imp 0 -
    - Make Board
    - Add piece
    - Get Piece

- Imp 1 - 
    - Make a fuction that iterates through every piece on the board and get the positions and sorts them to a collection by color.
        - it also should make a list of every valid move those pieces can make
    - Make a fuction to see if king is in check.
        - use list of valid moves to compare
    - Check if kings' start postion is any of the valid moves of the enemy team.
        - This checks for check
        - If so check if king has any valid moves
            - check if any of the pieces can block
                - if yes use fuction to see if after move king is still in check 
                    - if not add move to list
                - if it's good then end the fuction and return collections
                - if not checkmate
    - If not in check make a fuction that take a position and gets the chess piece and all the moves it can make
        - For every move see if it put king in check by going through valid moves of the other team
    - Check if there is any valid moves
        -if not: stalemate