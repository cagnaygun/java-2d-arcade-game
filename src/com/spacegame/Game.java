package com.spacegame;

import com.spacegame.view.GamePanel;

import javax.swing.JFrame;

public class Game {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Galaxy Blast");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            
            GamePanel panel = new GamePanel();
            frame.add(panel);
            frame.pack();
            
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}