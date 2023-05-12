package Gomokuuu;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.sql.*;


public class Server {
    public static final int PORT = 5190;
    public static final int MAX_PLAYERS = 2;

    private Socket[] players;
    private ServerSocket serverSocket;

    public static void main(String[] args) {
        new Server().start();
    }

    private void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Gomoku server started at port " + PORT);
            players = new Socket[MAX_PLAYERS];
    
            int authenticatedPlayers = 0;
            while (authenticatedPlayers < MAX_PLAYERS) {
                Socket player = serverSocket.accept();
                Scanner user_input = new Scanner(player.getInputStream());
                String username = user_input.nextLine();
                String password = user_input.nextLine();
    
                if (authenticate(username, password)) {
                    players[authenticatedPlayers] = player;
                    new PrintStream(player.getOutputStream()).println("1");
                    System.out.println(authenticatedPlayers);
                    new PrintStream(player.getOutputStream()).println(authenticatedPlayers + 1);
                    new Thread(new GameHandler(players[authenticatedPlayers], authenticatedPlayers + 1)).start();
                    authenticatedPlayers++;
                } else {
                    new PrintStream(player.getOutputStream()).println("0");
                    player.close();
                }
            }
            
            // Start the game after two players have been authenticated and connected.
            if (players[0] != null && players[1] != null) {
                System.out.println("Game started");
                PrintStream output1 = new PrintStream(players[0].getOutputStream());
                PrintStream output2 = new PrintStream(players[1].getOutputStream());
                output1.println("start");
                output2.println("start");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    private boolean authenticate(String username, String password) {
        try (java.sql.Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Gomoku", "root", "")) { 
            String query = "SELECT * FROM USERS WHERE username = ? AND password = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet resultSet = ps.executeQuery();
            boolean authenticated = resultSet.next();
            System.out.println("Authenticating user '" + username + "' with password '" + password + "': " + (authenticated ? "SUCCESS" : "FAILURE"));
            return authenticated;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private class GameHandler implements Runnable {
        private Socket playerSocket;
        private int playerNumber;

        public GameHandler(Socket playerSocket, int playerNumber) {
            this.playerSocket = playerSocket;
            this.playerNumber = playerNumber;
        }

        @Override
        public void run() {
            try {
                Scanner input = new Scanner(playerSocket.getInputStream());

                while (true) {
                    // receive input from client
                    String move = input.nextLine();
                    System.out.println("Player " + playerNumber + " move: " + move);
                    sendMove(move);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // send move to another client
        private void sendMove(String move) {
            for (Socket player : players) {
                try {
                    if (player != null && player != playerSocket) {
                        new PrintStream(player.getOutputStream()).println(move);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
