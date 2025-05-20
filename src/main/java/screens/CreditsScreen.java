package screens;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 *
 * @author Ricardo
 */
public class CreditsScreen extends JPanel{
    public CreditsScreen(JPanel cards, CardLayout cardLayout) {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));
        
        // Panel central para el contenido
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        contentPanel.setBackground(new Color(240, 240, 240));
        
        // Título
        JLabel title = new JLabel("Creditos", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Contenido de créditos
        JLabel credits1 = new JLabel("Chess Game Development", SwingConstants.CENTER);
        credits1.setFont(new Font("Arial", Font.PLAIN, 20));
        credits1.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel credits2 = new JLabel("Developed by: [Your Name]", SwingConstants.CENTER);
        credits2.setFont(new Font("Arial", Font.PLAIN, 18));
        credits2.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Botón de regreso
        JButton backButton = new JButton("Back to Main Menu");
        backButton.setFont(new Font("Arial", Font.PLAIN, 18));
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(e -> cardLayout.show(cards, "Menu"));
        
        // Espaciadores
        Component verticalStrut1 = Box.createVerticalStrut(30);
        Component verticalStrut2 = Box.createVerticalStrut(20);
        Component verticalStrut3 = Box.createVerticalStrut(40);
        
        // Agregar componentes al panel
        contentPanel.add(title);
        contentPanel.add(verticalStrut1);
        contentPanel.add(credits1);
        contentPanel.add(verticalStrut2);
        contentPanel.add(credits2);
        contentPanel.add(verticalStrut3);
        contentPanel.add(backButton);
        
        add(contentPanel, BorderLayout.CENTER);
    }
}
