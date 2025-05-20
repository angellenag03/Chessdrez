
package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import javax.swing.JFrame;
import screens.Game;
import sfx.SFXManager;

/**
 *
 * @author pausa
 */
public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.getContentPane().setBackground(new Color(33, 27, 47));
        frame.setLayout(new GridBagLayout());
        frame.setMinimumSize(new Dimension(800,00));
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        SFXManager sfx = SFXManager.getInstance();
        sfx.playSong("Main Theme.wav", "Check.wav");
        Game b = new Game();
        frame.add(b);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
    }
}
