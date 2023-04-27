package Gomokuuu;

public class GameLogic {
    private GameLogic gameLogic;
    private char[][] board;
    private char currentPlayer;
    private boolean gameOver;
    private boolean draw;
    public void checkForWin() {
        // Check for horizontal and vertical lines
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 11; j++) {
                if (board[i][j] == currentPlayer && board[i][j+1] == currentPlayer &&
                        board[i][j+2] == currentPlayer && board[i][j+3] == currentPlayer &&
                        board[i][j+4] == currentPlayer) {
                    gameOver = true;
                    return;
                }
                if (board[j][i] == currentPlayer && board[j+1][i] == currentPlayer &&
                        board[j+2][i] == currentPlayer && board[j+3][i] == currentPlayer &&
                        board[j+4][i] == currentPlayer) {
                    gameOver = true;
                    return;
                }
            }
        }

        // Check for diagonal lines
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++) {
                if (board[i][j] == currentPlayer && board[i+1][j+1] == currentPlayer &&
                        board[i+2][j+2] == currentPlayer && board[i+3][j+3] == currentPlayer &&
                        board[i+4][j+4] == currentPlayer) {
                    gameOver = true;
                    return;
                }
                if (board[i][14-j] == currentPlayer && board[i+1][13-j] == currentPlayer &&
                        board[i+2][12-j] == currentPlayer && board[i+3][11-j] == currentPlayer &&
                        board[i+4][10-j] == currentPlayer) {
                    gameOver = true;
                    return;
                }
            }
        }
    }
}
