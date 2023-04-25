package Gomokuuu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Game{
    public static void main(String[] args) throws IOException {
        Gomo g = new Gomo();
        g.setUp();
    }
}

class Gomo{
    static final int boardSize = 15;
    static final int cellSize = 32;
    JFrame jf;
    JPanel boardPanel;
    Container contentPane;
    public boolean[][] isPressedArray;
    JTextArea textShown;
    JPanel jp2;
    static JTextField text;

    void setUp() {
        jf = new JFrame("Gomoku Game!");
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setSize(boardSize * cellSize, boardSize * cellSize);
        jf.setLocationRelativeTo(null);

        // Initialize the board panel
        boardPanel = new JPanel(new GridLayout(boardSize, boardSize));
        isPressedArray = new boolean[15][15];
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                isPressedArray[i][j] = false;
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(cellSize, cellSize));
                button.addActionListener(new buttonPressed(i, j));
                boardPanel.add(button);
            }
        }

        textShown = new JTextArea(16, 18);
        textShown.setEditable(false);
        jp2 = new JPanel();
        jp2.setLayout(new BorderLayout());
        jp2.add(boardPanel, BorderLayout.SOUTH);
        jp2.add(textShown, BorderLayout.NORTH);

        jf.add(jp2);
        jf.setVisible(true);

        try {
            Socket s = new Socket("localhost", 5190);
            Scanner input = new Scanner(s.getInputStream());
        } catch (IOException ignored) {}
    }
}

class buttonPressed extends Gomo implements ActionListener{
    int i;
    int j;
    buttonPressed(int row, int col){
        i = row;
        j = col;
    }
    @Override
    public void actionPerformed(ActionEvent e){
        JButton source = (JButton) e.getSource();
        source.setOpaque(true);
        source.setContentAreaFilled(true);
        source.setBorderPainted(false);
        source.setBackground(Color.BLACK);
        isPressedArray[i][j] = true;
    }
}