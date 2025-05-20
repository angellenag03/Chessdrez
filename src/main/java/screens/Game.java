
package screens;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import main.Board;
import main.MoveHistoryPanel;

/**
 *
 * @author pausa
 */
public class Game extends JPanel {
    private Board board = new Board();
    private MoveHistoryPanel historyPanel = board.getMoveHistoryPanel();
    
    public Game() {
        this.setLayout(new BorderLayout());
        this.add(board, BorderLayout.CENTER);
        this.add(historyPanel, BorderLayout.EAST);
    }
    
    // Método para reiniciar el juego si es necesario
    public void resetGame() {
        // 1. Remover componentes existentes
        this.removeAll();

        // 2. Crear NUEVAS instancias (esto es clave)
        board = new Board();
        historyPanel = new MoveHistoryPanel(); // ¡No uses board.getMoveHistoryPanel()!
        board.setMoveHistoryPanel(historyPanel); // Enlaza el panel al tablero

        // 3. Reconstruir la interfaz
        this.setLayout(new BorderLayout());
        this.add(board, BorderLayout.CENTER);
        this.add(historyPanel, BorderLayout.EAST);

        // 4. Forzar actualización
        this.revalidate();
        this.repaint();

        // 5. Opcional: Notificar al tablero que debe resetearse
        board.loadBoard(); // Asegura que el tablero interno también se reinicie
}
}