
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
}
