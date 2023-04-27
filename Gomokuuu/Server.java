package Gomokuuu;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final int PORT = 5190;
    private static int playerCount = 0;
    private static Socket[] players = new Socket[2];
    private static int[][] board = new int[15][15];
    private static int currentPlayer = 1;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started, waiting for players to connect...");

            while (playerCount < 2) {
                Socket playerSocket = serverSocket.accept();
                playerCount++;
                players[playerCount - 1] = playerSocket;
                System.out.println("Player " + playerCount + " connected.");
                new GameHandler(playerSocket, playerCount).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class GameHandler extends Thread {
        private Socket socket;
        static int playerId;
        private boolean gameEnded = false;

        public GameHandler(Socket socket, int playerId) {
            this.socket = socket;
            this.playerId = playerId;
        }

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                while (!gameEnded) {
                    // Wait for the current player to make a move
                    if (playerId == currentPlayer) {
                        System.out.println("You turn");
                        out.println("YOUR_TURN");
                        String move = in.readLine();
                        System.out.println("Move: " + move);
                        if (move != null) {
                            String[] moveParts = move.split(",");
                            int x = Integer.parseInt(moveParts[0]);
                            int y = Integer.parseInt(moveParts[1]);
                            board[x][y] = playerId;
                            if (checkWin(x, y, board, playerId)) {
                                out.println("WIN");
                                gameEnded = true;
                            } else {
                                // Send the move to the other player
                                sendMove(move);
                                currentPlayer = (currentPlayer == 1) ? 2 : 1;
                            }
                        }
                    } else {
                        out.println("WAIT");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private boolean checkWin(int x, int y, int[][] board, int playerId) {
            int row = board.length;
            int col = board[0].length;

            // Check horizontal
            int count = 1;
            for (int i = y - 1; i >= 0 && board[x][i] == playerId; i--) {
                count++;
            }
            for (int i = y + 1; i < col && board[x][i] == playerId; i++) {
                count++;
            }
            if (count >= 5) return true;

            // Check vertical
            count = 1;
            for (int i = x - 1; i >= 0 && board[i][y] == playerId; i--) {
                count++;
            }
            for (int i = x + 1; i < row && board[i][y] == playerId; i++) {
                count++;
            }
            if (count >= 5) return true;

            // Check diagonal
            count = 1;
            for (int i = x - 1, j = y - 1; i >= 0 && j >= 0 && board[i][j] == playerId; i--, j--) {
                count++;
            }
            for (int i = x + 1, j = y + 1; i < row && j < col && board[i][j] == playerId; i++, j++) {
                count++;
            }
            if (count >= 5) return true;

            // Check anti-diagonal
            count = 1;
            for (int i = x - 1, j = y + 1; i >= 0 && j < col && board[i][j] == playerId; i--, j++) {
                count++;
            }
            for (int i = x + 1, j = y - 1; i < row && j >= 0 && board[i][j] == playerId; i++, j--) {
                count++;
            }
            if (count >= 5) return true;

            return false;
        }


        void sendMove(String move) {
            try {
                int otherPlayerId = (playerId == 1) ? 2 : 1;
                PrintWriter out = new PrintWriter(players[otherPlayerId - 1].getOutputStream(), true);
                out.println("MOVE:" + move);
                System.out.println("Send Move: " + move);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
