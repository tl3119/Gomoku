package Gomokuuu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
    boolean isPlayer2Turn;
    boolean isConnected = false;

    void setUp() {
        jf = new JFrame("Gomoku Game!");
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setSize(boardSize * cellSize + 1, boardSize * cellSize + 1);
        jf.setLocationRelativeTo(null);

        // Load stone images
        Game.blackStone = new ImageIcon("images/black_stone.png");
        Game.whiteStone = new ImageIcon("images/white_stone.png");

        // use this path if you run in any Java IDE: images/black_stone.png

        // Initialize the board panel
        boardPanel = new BoardPanel(this);
        boardPanel.addMouseListener(new BoardClickListener());

        jf.add(boardPanel);
        jf.setVisible(true);

        try {
            // networking
            socket = new Socket("localhost", 5190);
            input = new Scanner(socket.getInputStream());
            output = new PrintStream(socket.getOutputStream());
            currentPlayer = Integer.parseInt(input.nextLine());
            isPlayer1Turn = true;
            isPlayer2Turn = false;
            if (currentPlayer == 1) {
                JOptionPane waitDialog = new JOptionPane("Waiting for the second player to connect...", JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[] {}, null);
                JDialog waitWindow = waitDialog.createDialog("Waiting for the second player...");
                waitWindow.setModal(false);
                waitWindow.setVisible(true);
    
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (!isConnected) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        waitWindow.dispose();
                    }
                }).start();
            }    
            new Thread(new MoveReceiver()).start();
        } catch (IOException ignored) {
        }
    }

    // clients send move to the server
    void sendMove(int x, int y, int z, int w) {
        output.println(x + "," + y + "," + z + "," + w);
    }

    class MoveReceiver implements Runnable {
        // use thread concurrency
        @Override
        public void run() {
            while (true) {
                if (input.hasNextLine()) {
                    isConnected = true;
                    String message = input.nextLine();
                    if (message.equals("start")) {
                        isConnected = true;
                    } else {
                        String[] move = message.split(",");
                        int x = Integer.parseInt(move[0]);
                        int y = Integer.parseInt(move[1]);
                        int z = Integer.parseInt(move[2]);
                        int w = Integer.parseInt(move[3]);
                        if(w == 0){
                            int stoneValue = (currentPlayer == 1) ? 2 : 1;
                            boardPanel.updateBoard(x, y, stoneValue, z);
                        }
                        else if(w == currentPlayer){
                            JOptionPane.showMessageDialog(null, "Player "+w +" win", "Game over", JOptionPane.INFORMATION_MESSAGE);
                            break;
                        }else{
                            JOptionPane.showMessageDialog(null, "Player "+currentPlayer +" lose", "Game over", JOptionPane.INFORMATION_MESSAGE);
                            break;
                        }
                    }
                    
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

        // use Graphics to draw the board
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

        // update board information
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
            if (checkWin(x, y, board, z)) {
                String winnerMessage = (stoneValue == z) ? "Player "+ z +" win!" : "Player " + z + " lose.";
                JOptionPane.showMessageDialog(null, winnerMessage, "Game over", JOptionPane.INFORMATION_MESSAGE);
                repaint();
            }
            if(checkDraw(board)){
                String drawMessage = "This is a draw game!";
                JOptionPane.showMessageDialog(null, drawMessage, "Game over", JOptionPane.INFORMATION_MESSAGE);
                repaint();
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
        if (count >= 5)
            return true;

        // Check vertical
        count = 1;
        for (int i = x - 1; i >= 0 && board[i][y] == playerId; i--) {
            count++;
        }
        for (int i = x + 1; i < row && board[i][y] == playerId; i++) {
            count++;
        }
        if (count >= 5)
            return true;

        // Check diagonal
        count = 1;
        for (int i = x - 1, j = y - 1; i >= 0 && j >= 0 && board[i][j] == playerId; i--, j--) {
            count++;
        }
        for (int i = x + 1, j = y + 1; i < row && j < col && board[i][j] == playerId; i++, j++) {
            count++;
        }
        if (count >= 5)
            return true;

        // Check anti-diagonal
        count = 1;
        for (int i = x - 1, j = y + 1; i >= 0 && j < col && board[i][j] == playerId; i--, j++) {
            count++;
        }
        for (int i = x + 1, j = y - 1; i < row && j >= 0 && board[i][j] == playerId; i++, j--) {
            count++;
        }
        if (count >= 5)
            return true;

        return false;
    }
    private boolean checkDraw(int[][] board){
        for(int i = 0; i < board.length; ++i){
            for(int j = 0; j < board[i].length; ++j){
                if(board[i][j] == 0){
                    return false;
                }
            }
        }
        return true;
    }
    class BoardClickListener extends MouseAdapter {
    
        @Override
        public void mousePressed(MouseEvent e) {
            if (!isConnected) {
                // Ignore the click if not connected
                return;
            }
            int x = (e.getX() + Gomo.cellSize / 2) / Gomo.cellSize;
            int y = (e.getY() + Gomo.cellSize / 2) / Gomo.cellSize;
            if (x < Gomo.boardSize && y < Gomo.boardSize && ((BoardPanel) e.getSource()).board[x][y] == 0) {
                if (currentPlayer == 1 && isPlayer1Turn) {
                    ((BoardPanel) e.getSource()).updateBoard(x, y, 1, 1);
                    if(checkWin(x,y,((BoardPanel) e.getSource()).board, 1)){
                        sendMove(x, y, 1, 1);
                    }else{
                        sendMove(x, y, 1, 0);
                    }
//                    System.out.println("FIRST CLICK: " + isPlayer1Turn);
//                    System.out.println("after CLICK: " + isPlayer1Turn);

                } else if (currentPlayer == 2 && isPlayer2Turn) {
                    ((BoardPanel) e.getSource()).updateBoard(x, y, 2, 2);
                    if(checkWin(x,y,((BoardPanel) e.getSource()).board, 2)){
                        sendMove(x, y, 2, 2);
                    }else{
                        sendMove(x, y, 2, 0);
                    }
                }
            }
        }
    }
}

