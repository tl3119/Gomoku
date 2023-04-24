import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

public class Game{
    public static void main(String[] args) throws IOException {
        Gomo g = new Gomo();
        g.setUp();
    }
}

class Gomo{
    static final int boardSize = 15;
    static final int cellSize = 35;
    JFrame jf;
    JPanel boardPanel;
    Container contentPane;

    void setUp() {
        jf = new JFrame("Gomoku Game!");
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setSize(boardSize * cellSize, boardSize * cellSize);
        jf.setLocationRelativeTo(null);

        // Initialize the board panel
        boardPanel = new JPanel(new GridLayout(boardSize, boardSize));
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(cellSize, cellSize));
                boardPanel.add(button);
            }
        }

        jf.add(boardPanel);
        jf.setVisible(true);
    }
}