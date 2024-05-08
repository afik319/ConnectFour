# ConnectFour Game

This is an implementation of the classic Connect Four game using Java and JavaFX. The game allows two players to take turns dropping colored tokens into a grid, with the objective of being the first player to form a horizontal, vertical, or diagonal line of four tokens of their color.

## Features

- Interactive game board with a 6x7 grid
- Two-player mode with alternating turns
- Colored tokens (red and yellow) for each player
- Buttons for selecting the column to drop a token
- Keyboard support for selecting columns and clearing the board
- Visual indication of the winning line when a player wins
- Game over detection and announcement of the winner or a tie
- Sound effects for game start and game over
- Clear button to reset the game and start a new round

## How to Play

1. Launch the Connect Four game application.
2. The game starts with an empty board and the first player's turn (red).
3. Click on the buttons above the columns or press the corresponding number key (1-7) to select the column where you want to drop your token.
4. The token will be dropped to the lowest available position in the selected column.
5. Players take turns dropping their tokens until one player forms a line of four tokens of their color horizontally, vertically, or diagonally.
6. If a player wins, the winning line will be highlighted, and a winner announcement will be displayed.
7. If the board is completely filled without a winner, the game ends in a tie.
8. To start a new game, click the "Clear" button or press the "Delete" key.

## Running the Game

You can run the game using the provided runnable .jar file. Simply double-click on the file or execute it from the command line using the following command:

```bash
java -jar ConnectFour.jar
