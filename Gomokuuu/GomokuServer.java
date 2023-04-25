package Gomokuuu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
//
//public class GomokuServer{
//    static int currentPlayer;
//    static int[][] board;
//    private static final int BOARD_SIZE = 15;
//    private static final int PLAYER_ONE = 1;
//    private static final int PLAYER_TWO = 2;
//    private static final int EMPTY = 0;
//
//    private int[][] gameState;
//    private int currentPlayerId;
//    private boolean gameOver;
//
//    public static void main(String[] args){
//        currentPlayer = 1;
//        board = new int[15][15];
//
//    }
//    public synchronized void makeMove(int row, int col, int playerId) {
//        if (gameState[row][col] != EMPTY) {
//            // This move is not valid
//            return;
//        }
//
//        if (playerId != currentPlayerId) {
//            // It's not this player's turn
//            return;
//        }
//
//        gameState[row][col] = currentPlayerId;
//
//        // Check for a win
//        boolean hasWinner = checkForWin(row, col, currentPlayerId);
//        if (hasWinner) {
//            // There is a winner!
//            gameOver = true;
//            sendGameState();
//            return;
//        }
//
//        // Check for a draw
//        boolean hasEmptySpaces = false;
//        for (int i = 0; i < BOARD_SIZE; i++) {
//            for (int j = 0; j < BOARD_SIZE; j++) {
//                if (gameState[i][j] == EMPTY) {
//                    hasEmptySpaces = true;
//                    break;
//                }
//            }
//            if (hasEmptySpaces) {
//                break;
//            }
//        }
//        if (!hasEmptySpaces) {
//            // It's a draw
//            gameOver = true;
//            sendGameState();
//            return;
//        }
//
//        // Switch to the other player's turn
//        currentPlayerId = (currentPlayerId == PLAYER_ONE) ? PLAYER_TWO : PLAYER_ONE;
//        sendGameState();
//    }
//
//    private void sendGameState() {
//        // Send the board state and colors to all players
//        for (PrintWriter output : outputs) {
//            output.println("BOARD");
//            for (int i = 0; i < BOARD_SIZE; i++) {
//                for (int j = 0; j < BOARD_SIZE; j++) {
//                    int color = boardColors[i][j];
//                    if (color == 0) {
//                        output.print("_ ");
//                    } else if (color == 1) {
//                        output.print("B ");
//                    } else {
//                        output.print("L ");
//                    }
//                }
//                output.println();
//            }
//            output.println("CURRENT_PLAYER " + (currentPlayer + 1));
//        }
//    }
//
//    private boolean checkForWin(int row, int col, int playerId) {
//        // Check for a horizontal win
//        int count = 0;
//        for (int c = col - 4; c <= col + 4; c++) {
//            if (c < 0 || c >= BOARD_SIZE) {
//                continue;
//            }
//            if (gameState[row][c] == playerId) {
//                count++;
//            } else {
//                count = 0;
//            }
//            if (count >= 5) {
//                return true;
//            }
//        }
//
//        // Check for a vertical win
//        count = 0;
//        for (int r = row - 4; r <= row + 4; r++) {
//            if (r < 0 || r >= BOARD_SIZE) {
//                continue;
//            }
//            if (gameState[r][col] == playerId) {
//                count++;
//            } else {
//                count = 0;
//            }
//            if (count >= 5) {
//                return true;
//            }
//        }
//
//        // Check for a diagonal win (top-left to bottom-right)
//        count = 0;
//        for (int i = -4; i <= 4; i++) {
//            int r = row + i;
//            int c = col + i;
//            if (r < 0 || r >= BOARD_SIZE || c < 0 || c >= BOARD_SIZE) {
//                continue;
//            }
//            if (gameState[r][c] == playerId) {
//                count++;
//            } else {
//                count = 0;
//            }
//            if (count >= 5) {
//                return true;
//            }
//        }
//
//        // Check for a diagonal win (bottom-left to top-right)
//        count = 0;
//        for (int i = -4; i <= 4; i++) {
//            int r = row - i;
//            int c = col + i;
//            if (r < 0 || r >= BOARD_SIZE || c < 0 || c >= BOARD_SIZE) {
//                continue;
//            }
//            if (gameState[r][c] == playerId) {
//                count++;
//            } else {
//                count = 0;
//            }
//            if (count >= 5) {
//                return true;
//            }
//        }
//
//        // If none of the above conditions are met, then there is no win
//        return false;
//    }
//
//
//}
//
//class Connection extends buttonPressed implements Runnable{
//    Socket sock;
//    static ArrayList<Socket> users = new ArrayList<>();
//    Connection(int i, int j, Socket data) {
//        super(i, j);
//        sock = data; }
//    public void run(){
//        while(true){
//            if(isPressedArray[i][j] == true){
//
//            }
//        }
//    }
//}
//
//class PlayerHandler implements Runnable {
//    private Socket socket;
//    private BufferedReader input;
//    private PrintWriter output;
//    private GomokuServer server;
//    private int playerId;
//
//    public PlayerHandler(Socket socket, GomokuServer server, int playerId) {
//        this.socket = socket;
//        this.server = server;
//        this.playerId = playerId;
//
//        try {
//            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            output = new PrintWriter(socket.getOutputStream(), true);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void run() {
//        try {
//            // Send the player ID to the client
//            output.println("PLAYER_ID " + playerId);
//
//            // Wait for the client to send moves
//            while (true) {
//                String message = input.readLine();
//                if (message == null) {
//                    // Client has disconnected
//                    return;
//                }
//                if (message.startsWith("MOVE")) {
//                    int row = Integer.parseInt(message.split(" ")[1]);
//                    int col = Integer.parseInt(message.split(" ")[2]);
//                    //server.makeMove(row, col, playerId);
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                socket.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public void sendMessage(String message) {
//        output.println(message);
//    }
//}

//public class GomokuServer {
//    private static final int PORT = 8000;
//    private static final int MAX_PLAYERS = 2;
//
//    private int currentPlayer;
//    private int[][] board;
//    private List<PlayerHandler> players;
//
//    public GomokuServer() {
//        // Initialize the game state
//        currentPlayer = 1;
//        board = new int[15][15];
//        players = new ArrayList<>();
//
//        // Start the server
//        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
//            System.out.println("Server started on port " + PORT);
//
//            while (true) {
//                // Wait for players to connect
//                if (players.size() < MAX_PLAYERS) {
//                    System.out.println("Waiting for player " + (players.size() + 1) + " to connect...");
//                    Socket playerSocket = serverSocket.accept();
//                    PlayerHandler playerHandler = new PlayerHandler(playerSocket);
//                    players.add(playerHandler);
//                    System.out.println("Player " + players.size() + " connected");
//
//                    // Start a new thread to handle the player
//                    Thread thread = new Thread(playerHandler);
//                    thread.start();
//
//                    // Notify the player of their color and the current player
//                    playerHandler.send(String.format("COLOR %d", players.size()));
//                    playerHandler.send(String.format("TURN %d", currentPlayer));
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void main(String[] args) {
//        new GomokuServer();
//    }
//
//    private class PlayerHandler implements Runnable {
//        private Socket socket;
//        private int playerNumber;
//
//        public PlayerHandler(Socket socket) {
//            this.socket = socket;
//        }
//
//        public void send(String message) {
//            try {
//                socket.getOutputStream().write((message + "\n").getBytes());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        @Override
//        public void run() {
//            try {
//                while (true) {
//                    // Receive the player's move
//                    String move = receive();
//                    int row = Integer.parseInt(move.split(" ")[0]);
//                    int col = Integer.parseInt(move.split(" ")[1]);
//
//                    // Update the game state and notify the other players
//                    board[row][col] = playerNumber;
//                    for (PlayerHandler player : players) {
//                        if (player != this) {
//                            player.send(String.format("MOVE %d %d %d", playerNumber, row, col));
//                            player.send(String.format("TURN %d", currentPlayer));
//                        }
//                    }
//
//                    // Check for a win or draw
//                    if (isWin(playerNumber, row, col)) {
//                        for (PlayerHandler player : players) {
//                            player.send(String.format("WIN %d", playerNumber));
//                        }
//                        break;
//                    } else if (isDraw()) {
//                        for (PlayerHandler player : players) {
//                            player.send("DRAW");
//                        }
//                        break;
//                    }
//
//                    // Update the current player
//                    currentPlayer = currentPlayer == 1 ? 2 : 1;
//                    send(String.format("TURN %d", currentPlayer));
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    socket.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        private String receive() throws IOException {
//            StringBuilder sb = new StringBuilder();
//            int c;
//            while ((c = socket.getInputStream().read()) != -1) {
//                if (c == '\n')
//
