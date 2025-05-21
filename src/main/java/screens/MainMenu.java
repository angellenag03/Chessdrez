package screens;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import utils.FontLoader;
import sfx.SFXManager;

/**
 *
 * @author angelsn & ricardo
 */
public class MainMenu extends JPanel {
    private CardLayout cardLayout;
    private JPanel cards;
    private Font alagardFontLarge;
    private Font alagardFontMedium;
    private Clip hoverSound;
    private Clip clickSound;
    private SFXManager sfx;
    
    public MainMenu(JPanel cards, CardLayout cardLayout) {
        // Cargar la fuente Alagard
        try {
            alagardFontLarge = FontLoader.loadFont(36f);
            alagardFontMedium = FontLoader.loadFont(24f);
        } catch (IOException e) {
            e.printStackTrace();
            // Fuentes de respaldo
            alagardFontLarge = new Font("Century Gothic", Font.BOLD, 36);
            alagardFontMedium = new Font("Century Gothic", Font.PLAIN, 24);
        }
        
        // Obtener instancia del SFXManager
        sfx = SFXManager.getInstance();
         
        this.cardLayout = cardLayout;
        this.cards = cards;
        
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);
        
        // Panel central para los botones
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 1, 0, 30));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(50, 150, 100, 150));
        buttonPanel.setBackground(Color.BLACK);
        
        // Cargar el logo
        ImageIcon logoIcon = new ImageIcon(getClass().getClassLoader().getResource("logo.png"));
        if (logoIcon.getImageLoadStatus() == java.awt.MediaTracker.COMPLETE) {
            Image logoImage = logoIcon.getImage();
            Image scaledLogo = logoImage.getScaledInstance(400, 200, Image.SCALE_SMOOTH);
            logoIcon = new ImageIcon(scaledLogo);
        } else {
            System.err.println("No se pudo cargar el logo");
        }
        
        JLabel logoLabel = new JLabel(logoIcon);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 50, 0));
        
        // Botón Iniciar Juego
        JButton startButton = createMenuButton("START GAME");
        startButton.addActionListener(e -> {
            sfx.playSound("hover");
            showGameModeSelection();
        });
        
        // Botón Créditos
        JButton creditsButton = createMenuButton("CREDITS");
        creditsButton.addActionListener(e -> {
            sfx.playSound("hover");
            cardLayout.show(cards, "Credits");
        });
        
        buttonPanel.add(startButton);
        buttonPanel.add(creditsButton);
        
        add(logoLabel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
    }
    
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(alagardFontMedium);
        button.setPreferredSize(new Dimension(300, 70));
        button.setBackground(new Color(70, 50, 20));
        button.setForeground(new Color(200, 160, 60));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 160, 60), 3),
            BorderFactory.createEmptyBorder(10, 25, 10, 25)
        ));
        
        // Efecto hover con reproducción de sonido
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(90, 70, 40));
                button.setForeground(new Color(220, 180, 80));
                
                // Método 1: Usando la instancia local de sonido
                sfx.playSound("hover");
                
                // Método 2: Alternativa usando SFXManager
                // Descomenta esta línea para usar SFXManager en lugar de la reproducción local
                // sfxManager.playSound("hover");
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(70, 50, 20));
                button.setForeground(new Color(200, 160, 60));
            }
        });
        
        return button;
    }
    
    private void showGameModeSelection() {
        // Configurar el estilo del JOptionPane
        UIManager.put("OptionPane.background", Color.BLACK);
        UIManager.put("Panel.background", Color.BLACK);
        UIManager.put("OptionPane.messageForeground", new Color(200, 160, 60));
        UIManager.put("Button.background", new Color(70, 50, 20));
        UIManager.put("Button.foreground", new Color(200, 160, 60));
        UIManager.put("Button.font", alagardFontMedium);
        
        Object[] options = {"Standard (8x8)", "Express (7x7)", "Rush (6x6)"};
        int choice = JOptionPane.showOptionDialog(this,
            "Select game mode:",
            "Game Mode",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]);
        
        // Restaurar valores por defecto
        UIManager.put("OptionPane.background", null);
        UIManager.put("Panel.background", null);
        UIManager.put("OptionPane.messageForeground", null);
        UIManager.put("Button.background", null);
        UIManager.put("Button.foreground", null);
        UIManager.put("Button.font", null);
        
        if (choice == 0) {
            cardLayout.show(cards, "Game");
        } else if (choice != JOptionPane.CLOSED_OPTION) {
            JOptionPane.showMessageDialog(this, 
                "Only Standard mode is available at this time", 
                "Mode Not Available", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
}