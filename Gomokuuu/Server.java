// CS-UY 3913: Java and Web Design - Final Project
// Team member: Tianzuo Liu(tl3119@nyu.edu), Tianyi Zhang(tz2020@nyu.edu)

package Gomokuuu;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

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
