package screens;

import javax.swing.*;
import java.awt.*;
import utils.FontLoader;
import java.io.IOException;

public class CreditsScreen extends JPanel {
    private Font alagardFontLarge;
    private Font alagardFontMedium;
    private Font alagardFontSmall;
    
    public CreditsScreen(JPanel cards, CardLayout cardLayout) {
        // Cargar la fuente Alagard en diferentes tamaños
        try {
            alagardFontLarge = FontLoader.loadFont(36f);
            alagardFontMedium = FontLoader.loadFont(24f);
            alagardFontSmall = FontLoader.loadFont(18f);
        } catch (IOException e) {
            e.printStackTrace();
            // Fuentes de respaldo si hay error
            alagardFontLarge = new Font("Century Gothic", Font.BOLD, 36);
            alagardFontMedium = new Font("Century Gothic", Font.PLAIN, 24);
            alagardFontSmall = new Font("Century Gothic", Font.PLAIN, 18);
        }
        
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);  // Fondo negro para mejor contraste
        
        // Panel central para el contenido
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        contentPanel.setBackground(Color.BLACK);
        
        // Título con estilo medieval
        JLabel title = new JLabel("CREDITS", SwingConstants.CENTER);
        title.setFont(alagardFontLarge);
        title.setForeground(new Color(200, 160, 60));  // Color dorado medieval
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        
        // Línea decorativa
        JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);
        separator.setForeground(new Color(200, 160, 60));
        separator.setMaximumSize(new Dimension(400, 5));
        
        // Contenido de créditos con estilo
        JLabel gameTitle = new JLabel("ADREJEZ", SwingConstants.CENTER);
        gameTitle.setFont(alagardFontMedium);
        gameTitle.setForeground(Color.WHITE);
        gameTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel developedBy = new JLabel("Developed by:", SwingConstants.CENTER);
        developedBy.setFont(alagardFontSmall);
        developedBy.setForeground(new Color(180, 180, 180));
        developedBy.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel author1 = new JLabel("Ricardo ID: 0000024700", SwingConstants.CENTER);
        author1.setFont(alagardFontMedium);
        author1.setForeground(new Color(200, 160, 60));
        author1.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel author2 = new JLabel("angelsn ID: 00000248553", SwingConstants.CENTER);
        author2.setFont(alagardFontMedium);
        author2.setForeground(new Color(200, 160, 60));
        author2.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Botón de regreso con estilo
        JButton backButton = new JButton("RETURN TO MENU");
        backButton.setFont(alagardFontSmall);
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setBackground(new Color(70, 50, 20));
        backButton.setForeground(Color.WHITE);
        backButton.setBorder(BorderFactory.createLineBorder(new Color(200, 160, 60), 2));
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> cardLayout.show(cards, "Menu"));
        
        // Espaciadores
        Component verticalStrut = Box.createVerticalStrut(20);
        
        // Agregar componentes al panel
        contentPanel.add(title);
        contentPanel.add(separator);
        contentPanel.add(Box.createVerticalStrut(30));
        contentPanel.add(gameTitle);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(developedBy);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(author1);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(author2);
        contentPanel.add(Box.createVerticalGlue());
        contentPanel.add(backButton);
        
        add(contentPanel, BorderLayout.CENTER);
    }
}