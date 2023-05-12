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

            for (int i = 0; i < MAX_PLAYERS; i++) {
                players[i] = serverSocket.accept();
                System.out.println("Player " + (i + 1) + " connected");

                if (i == 0) {
                    new PrintStream(players[i].getOutputStream()).println("1");
                } else {
                    new PrintStream(players[i].getOutputStream()).println("2");
                }

                new Thread(new GameHandler(players[i], i + 1)).start();
            }

            if (players[0] != null && players[1] != null) {
                PrintStream output1 = new PrintStream(players[0].getOutputStream());
                PrintStream output2 = new PrintStream(players[1].getOutputStream());
                output1.println("start");
                output2.println("start");
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean checkLogin(String username, String password) {
        try {
            // Assuming you're running MySQL on localhost:3306 and database name is mydb
            String url = "jdbc:mysql://localhost:3306/Gomoku";
            String dbUsername = "root";  // replace with your MySQL username
            String dbPassword = "";  // replace with your MySQL password
    
            // Establish a connection
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
    
            // Prepared statement to prevent SQL Injection
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
            ps.setString(1, username);
            ps.setString(2, password);
            
            ResultSet rs = ps.executeQuery();
            
            // If result set is not empty, login is valid
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
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

                while (input.hasNextLine()) {
                    String received = input.nextLine();
                    
                    if (received.startsWith("LOGIN:")) {
                        String[] loginDetails = received.split(":")[1].split(",");
                        String username = loginDetails[0];
                        String password = loginDetails[1];
        
                        if (checkLogin(username, password)) {
                            new PrintStream(playerSocket.getOutputStream()).println("LOGIN_SUCCESS");
                            System.out.println("Player " + playerNumber + " logged in as " + username);
                        } else {
                            new PrintStream(playerSocket.getOutputStream()).println("LOGIN_FAILED");
                        }
                    } else {
                        // Normal game handling code
                        String move = received;
                        System.out.println("Player " + playerNumber + " move: " + move);
                        sendMove(move);
                    }
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
