package main;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import screens.CreditsScreen;
import screens.Game;
import screens.MainMenu;
import sfx.SFXManager;

/**
 *
 * @author Ricardo
 */
public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel cards;
    
    public MainFrame() {
        setTitle("ADREJEZ");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);
        
        // Crear las diferentes pantallas
        MainMenu mainMenu = new MainMenu(cards, cardLayout);
        Game gameScreen = new Game();
        CreditsScreen creditsScreen = new CreditsScreen(cards, cardLayout);
        
        // Agregar las pantallas al CardLayout
        cards.add(mainMenu, "Menu");
        cards.add(gameScreen, "Game");
        cards.add(creditsScreen, "Credits");
        
        add(cards);
        cardLayout.show(cards, "Menu");
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.getContentPane().setBackground(new Color(33, 27, 47));
            frame.setLayout(new GridBagLayout());
            frame.setMinimumSize(new Dimension(800,00));
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            SFXManager sfx = SFXManager.getInstance();
            sfx.playSong("Main Theme.wav", "Check.wav");
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setVisible(true);

            frame.setVisible(true);
        });
    }
}
