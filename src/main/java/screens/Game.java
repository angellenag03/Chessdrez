
package screens;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import main.Board;
import main.MoveHistoryPanel;

/**
 *
 * @author angelsn & ricardo
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
        this.removeAll();
        board = new Board();
        historyPanel = board.getMoveHistoryPanel();
        this.add(board, BorderLayout.CENTER);
        this.add(historyPanel, BorderLayout.EAST);
        this.revalidate();
        this.repaint();
    }
}