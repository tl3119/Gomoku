package Gomokuuu;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    public static void main(String[] args){
        try {
            ServerSocket socket = new ServerSocket(5190);
            System.out.println("Connection begin");
            while(true) {
                System.out.println("Server is ready to receive");
                Socket client = socket.accept();
                System.out.println("Got a connection from: "+client.getInetAddress());
                new Connection(client).start();
            }
        } catch(IOException ex) {
            System.out.println("Unable to bind to port 5190");
        }
    }
}

class Connection extends Thread{
    static ArrayList<Socket> players = new ArrayList<>();
    Socket sock;
    String username;
    static final int MAX_PLAYERS = 2;
    Connection(Socket data) { sock = data; }
    @Override
    public void run(){
        try{
            PrintStream out = new PrintStream(sock.getOutputStream());

            players.add(sock);
            // Wait for players to connect
            if (players.size() < MAX_PLAYERS) {
                System.out.println("Waiting for another player to connect...");
            }else if(players.size() == MAX_PLAYERS){
                System.out.println("You can play now!");
            }

        }catch (Exception ignored){

        }
    }
}