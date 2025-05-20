package screens;

import static java.awt.AWTEventMulticaster.add;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 *
 * @author Ricardo
 */
public class MainMenu extends JPanel{
private CardLayout cardLayout;
    private JPanel cards;

    public MainMenu(JPanel cards, CardLayout cardLayout) {
        this.cardLayout = cardLayout;
        this.cards = cards;
        
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));
        
        // Panel central para los botones
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 1, 0, 20));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(100, 200, 100, 200));
        buttonPanel.setBackground(new Color(240, 240, 240));
        
        // Título
        JLabel title = new JLabel("Chess Game", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 50, 0));
        
        // Botón Iniciar Juego
        JButton startButton = createMenuButton("Start Game");
        startButton.addActionListener(e -> showGameModeSelection());
        
        // Botón Créditos
        JButton creditsButton = createMenuButton("Credits");
        creditsButton.addActionListener(e -> cardLayout.show(cards, "Credits"));
        
        buttonPanel.add(startButton);
        buttonPanel.add(creditsButton);
        
        add(title, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
    }
    
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 24));
        button.setPreferredSize(new Dimension(200, 60));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        return button;
    }
    
    private void showGameModeSelection() {
        Object[] options = {"Standard (8x8)", "Express (7x7)", "Rush (6x6)"};
        int choice = JOptionPane.showOptionDialog(this,
            "Select game mode:",
            "Game Mode",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]);
        
        // Por ahora solo implementamos Standard
        if (choice == 0) {
            cardLayout.show(cards, "Game");
        } else {
            JOptionPane.showMessageDialog(this, 
                "Only Standard mode is available at this time", 
                "Mode Not Available", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
}