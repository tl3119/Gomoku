package Gomokuuu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class Game {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Gomo g = new Gomo();
                g.setUp();
            }
        });
    }

    static ImageIcon blackStone;
    static ImageIcon whiteStone;
}

class Gomo {
    static final int boardSize = 15;
    static final int cellSize = 32;
    JFrame jf;
    BoardPanel boardPanel;
    Scanner input;
    PrintStream output;
    Socket socket;
    int currentPlayer;
    boolean isPlayer1Turn;
    boolean isWaiting;
    boolean isPlayer2Turn;

    void setUp() {
        jf = new JFrame("Gomoku Game!");
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setSize(boardSize * cellSize + 1, boardSize * cellSize + 1);
        jf.setLocationRelativeTo(null);

        // Load stone images
        Game.blackStone = new ImageIcon("/Users/liutianzuo/Desktop/Gomoku/images/black_stone.png");
        Game.whiteStone = new ImageIcon("/Users/liutianzuo/Desktop/Gomoku/images/white_stone.png");

        File file = new File("images/black_stone.png");
        System.out.println(file.getAbsolutePath());

        // Initialize the board panel
        boardPanel = new BoardPanel(this);
        boardPanel.addMouseListener(new BoardClickListener());

        jf.add(boardPanel);
        jf.setVisible(true);

        try {
            socket = new Socket("localhost", 5190);
            input = new Scanner(socket.getInputStream());
            output = new PrintStream(socket.getOutputStream());
            currentPlayer = Integer.parseInt(input.nextLine());
            isPlayer1Turn = currentPlayer == 1;
            isPlayer2Turn = currentPlayer == 2;
//            isPlayer1Turn = true;
//            isPlayer2Turn = false;
            //isWaiting = !isPlayer1Turn;

            new Thread(new MoveReceiver()).start();
        } catch (IOException ignored) {
        }
    }

    void sendMove(int x, int y, int z) {
        output.println(x + "," + y + "," + z);
    }

    class MoveReceiver implements Runnable {
        @Override
        public void run() {
            while (true) {
                if (input.hasNextLine()) {
                    String[] move = input.nextLine().split(",");
                    int x = Integer.parseInt(move[0]);
                    int y = Integer.parseInt(move[1]);
                    int z = Integer.parseInt(move[2]);
                    int stoneValue = (currentPlayer == 1) ? 2 : 1;
                    boardPanel.updateBoard(x, y, stoneValue, z);
//                    isWaiting = false;
//                    isPlayer1Turn = !isPlayer1Turn;
                }
            }
        }
    }
    class BoardPanel extends JPanel {
        int[][] board;
        Gomo gomo;
    
        public BoardPanel(Gomo gomo) {
            super();
            board = new int[Gomo.boardSize][Gomo.boardSize];
            this.gomo = gomo;
        }
    
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int gridSize = Gomo.boardSize * Gomo.cellSize;
            g.setColor(Color.BLACK);
            for (int i = 0; i <= Gomo.boardSize; i++) {
                g.drawLine(i * Gomo.cellSize, 0, i * Gomo.cellSize, gridSize);
                g.drawLine(0, i * Gomo.cellSize, gridSize, i * Gomo.cellSize);
            }
    
            for (int i = 0; i < Gomo.boardSize; i++) {
                for (int j = 0; j < Gomo.boardSize; j++) {
                    if (board[i][j] == 1) {
                        g.drawImage(Game.blackStone.getImage(), i * Gomo.cellSize - Game.blackStone.getIconWidth() / 2, j * Gomo.cellSize - Game.blackStone.getIconHeight() / 2, null);
                    } else if (board[i][j] == 2) {
                        g.drawImage(Game.whiteStone.getImage(), i * Gomo.cellSize - Game.whiteStone.getIconWidth() / 2, j * Gomo.cellSize - Game.whiteStone.getIconHeight() / 2, null);
                    }
                }
            }
        }
    
        public void updateBoard(int x, int y, int stoneValue, int z) {
            board[x][y] = stoneValue;
            repaint();
            if(z == 1){
                isPlayer1Turn = false;
                isPlayer2Turn = true;
            }else{
                isPlayer2Turn = false;
                isPlayer1Turn = true;
            }
        }
    }
    class BoardClickListener extends MouseAdapter {
        private boolean blackTurn = true;
    
        @Override
        public void mousePressed(MouseEvent e) {
            int x = (e.getX() + Gomo.cellSize / 2) / Gomo.cellSize;
            int y = (e.getY() + Gomo.cellSize / 2) / Gomo.cellSize;
            if (x < Gomo.boardSize && y < Gomo.boardSize && ((BoardPanel) e.getSource()).board[x][y] == 0) {
                if(currentPlayer == 1 && isPlayer1Turn){
                    int stoneValue = (currentPlayer == 1) ? 1 : 2;
                    ((BoardPanel) e.getSource()).updateBoard(x, y, stoneValue, 1);
                    sendMove(x, y, 1);
                    System.out.println("FIRST CLICK: " + isPlayer1Turn);
                    System.out.println("after CLICK: " + isPlayer1Turn);

                }else if(currentPlayer == 2 && isPlayer2Turn){
                    int stoneValue = (currentPlayer == 1) ? 1 : 2;
                    ((BoardPanel) e.getSource()).updateBoard(x, y, stoneValue, 2);
                    sendMove(x, y, 2);
                    System.out.println("FIRST CLICK: " + isPlayer2Turn);
                }else{
                    isPlayer1Turn = isPlayer1Turn;
                    isPlayer2Turn = isPlayer2Turn;
                }
//                if (currentPlayer == 1) {
//                    isPlayer1Turn = false;
//                    isPlayer2Turn = true;
//                    System.out.println("after CLICK: " + isPlayer1Turn);
//                } else {
//                    isPlayer1Turn = true;
//                    isPlayer2Turn = false;
//                }
            }
    
//            if (x < Gomo.boardSize && y < Gomo.boardSize && ((BoardPanel) e.getSource()).board[x][y] == 0 && currentPlayer == 1 && isPlayer1Turn) {
////                int stoneValue = blackTurn ? 1 : 2;
////                ((BoardPanel) e.getSource()).updateBoard(x, y, stoneValue);
////                blackTurn = !blackTurn;
////
////                // Send the move to the server
////                Gomo g = ((BoardPanel) e.getSource()).gomo;
////                g.sendMove(x, y);
//                boardPanel.updateBoard(x, y, 1);
//                sendMove(x, y);
//                isPlayer1Turn = false;
//                isPlayer2Turn = true;
//            }else if(x < Gomo.boardSize && y < Gomo.boardSize && ((BoardPanel) e.getSource()).board[x][y] == 0 && currentPlayer == 2 && isPlayer2Turn){
//                boardPanel.updateBoard(x, y, 2);
//                sendMove(x, y);
//                isPlayer2Turn = false;
//                isPlayer1Turn = true;
//            }
        }
    }
}

