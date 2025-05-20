package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

/**
 * Panel que muestra el historial de movimientos del juego de ajedrez.
 * Utiliza notación algebraica para representar los movimientos.
 */
public class MoveHistoryPanel extends JPanel {
    private JTextArea moveHistoryText;
    private JScrollPane scrollPane;
    private JPanel movesPanel;
    private int currentMoveNumber = 1;
    
    // Constantes para el estilo
    private static final Color BACKGROUND_COLOR = new Color(240, 240, 240);
    private static final Color PANEL_COLOR = new Color(255, 255, 255);
    private static final Color BORDER_COLOR = new Color(200, 200, 200);
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 16);
    private static final Font MOVE_FONT = new Font("Arial", Font.PLAIN, 14);
    private static final Font NUMBER_FONT = new Font("Arial", Font.BOLD, 14);
    private static final int PADDING = 10;
    private static final int MOVE_SPACING = 5;

    /**
     * Constructor que inicializa el panel de historial de movimientos.
     * Configura el diseño y los componentes visuales.
     */
    public MoveHistoryPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(200, 400));
        setBackground(BACKGROUND_COLOR);
        
        // Panel para los números de jugada
        JPanel numbersPanel = new JPanel();
        numbersPanel.setLayout(new BoxLayout(numbersPanel, BoxLayout.Y_AXIS));
        numbersPanel.setBackground(PANEL_COLOR);
        numbersPanel.setBorder(BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING));
        
        // Panel para las jugadas
        movesPanel = new JPanel();
        movesPanel.setLayout(new BoxLayout(movesPanel, BoxLayout.Y_AXIS));
        movesPanel.setBackground(PANEL_COLOR);
        movesPanel.setBorder(BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING));
        
        // Panel contenedor con scroll
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(PANEL_COLOR);
        contentPanel.add(numbersPanel, BorderLayout.WEST);
        contentPanel.add(movesPanel, BorderLayout.CENTER);
        
        // Añadir borde al panel de contenido
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING)
        ));
        
        scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        add(scrollPane, BorderLayout.CENTER);
        
        // Título con estilo mejorado
        JLabel title = new JLabel("Move History", SwingConstants.CENTER);
        title.setFont(TITLE_FONT);
        title.setBorder(BorderFactory.createEmptyBorder(PADDING, 0, PADDING, 0));
        add(title, BorderLayout.NORTH);
    }

    /**
     * Actualiza el historial de movimientos con la nueva lista de FENs.
     * @param fenHistory Lista de strings FEN que representan el estado del tablero en cada movimiento
     */
    public void updateMoveHistory(List<String> fenHistory) {
        movesPanel.removeAll();
        currentMoveNumber = 1;
        
        JPanel currentMovePanel = new JPanel();
        currentMovePanel.setLayout(new FlowLayout(FlowLayout.LEFT, MOVE_SPACING, 0));
        currentMovePanel.setBackground(PANEL_COLOR);
        
        // Número de jugada con estilo
        JLabel numberLabel = new JLabel(currentMoveNumber + ".");
        numberLabel.setFont(NUMBER_FONT);
        numberLabel.setForeground(new Color(100, 100, 100));
        currentMovePanel.add(numberLabel);
        
        for (int i = 1; i < fenHistory.size(); i++) {
            String move = convertFENtoMove(fenHistory.get(i-1), fenHistory.get(i));
            
            if (i % 2 == 1) { // Jugada blanca
                JLabel moveLabel = createMoveLabel(move);
                currentMovePanel.add(moveLabel);
            } else { // Jugada negra
                JLabel moveLabel = createMoveLabel(move);
                currentMovePanel.add(moveLabel);
                
                // Añadir el panel actual y crear uno nuevo
                movesPanel.add(currentMovePanel);
                currentMovePanel = new JPanel();
                currentMovePanel.setLayout(new FlowLayout(FlowLayout.LEFT, MOVE_SPACING, 0));
                currentMovePanel.setBackground(PANEL_COLOR);
                
                // Incrementar el número y crear nuevo label
                currentMoveNumber++;
                numberLabel = new JLabel(currentMoveNumber + ".");
                numberLabel.setFont(NUMBER_FONT);
                numberLabel.setForeground(new Color(100, 100, 100));
                currentMovePanel.add(numberLabel);
            }
        }
        
        // Añadir última jugada si es necesario
        if (fenHistory.size() % 2 == 0) {
            movesPanel.add(currentMovePanel);
        }
        
        // Añadir un panel vacío al final para mejor espaciado
        movesPanel.add(Box.createVerticalStrut(PADDING));
        
        revalidate();
        repaint();
        
        // Auto-scroll al final
        JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
    }

    /**
     * Crea un label para un movimiento con el estilo apropiado.
     */
    private JLabel createMoveLabel(String move) {
        JLabel label = new JLabel(move);
        label.setFont(MOVE_FONT);
        
        // Añadir tooltip con información del movimiento
        label.setToolTipText("Move: " + move);
        
        // Añadir padding
        label.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        
        return label;
    }

    /**
     * Convierte la diferencia entre dos estados FEN en notación algebraica de ajedrez.
     * @param prevFEN FEN del estado anterior del tablero
     * @param currentFEN FEN del estado actual del tablero
     * @return String representando el movimiento en notación algebraica
     */
    private String convertFENtoMove(String prevFEN, String currentFEN) {
        String[] prevParts = prevFEN.split(" ");
        String[] currentParts = currentFEN.split(" ");
        
        // Obtener la colocación de piezas de ambos estados
        String[] prevRanks = prevParts[0].split("/");
        String[] currentRanks = currentParts[0].split("/");
        
        // Encontrar la pieza que se movió y su nueva posición
        int fromCol = -1, fromRow = -1;
        int toCol = -1, toRow = -1;
        char movedPiece = ' ';
        boolean isCapture = false;
        
        // Primero, encontrar la pieza que se movió
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                char prevPiece = getPieceAt(prevRanks, col, row);
                char currentPiece = getPieceAt(currentRanks, col, row);
                
                if (prevPiece != currentPiece) {
                    if (prevPiece != '1' && currentPiece == '1') {
                        // Encontramos la posición de origen
                        fromCol = col;
                        fromRow = row;
                        movedPiece = prevPiece;
                    } else if (prevPiece == '1' && currentPiece != '1') {
                        // Encontramos la posición de destino
                        toCol = col;
                        toRow = row;
                    } else if (prevPiece != '1' && currentPiece != '1' && prevPiece != currentPiece) {
                        // Es una captura
                        toCol = col;
                        toRow = row;
                        isCapture = true;
                        movedPiece = currentPiece;
                    }
                }
            }
        }
        
        // Si no encontramos el movimiento, retornar "???"
        if (fromCol == -1 || toCol == -1) {
            return "???";
        }
        
        // Construir la notación del movimiento
        StringBuilder move = new StringBuilder();
        
        // Añadir símbolo de la pieza (excepto para peones)
        if (Character.toUpperCase(movedPiece) != 'P') {
            move.append(Character.toUpperCase(movedPiece));
        }
        
        // Añadir símbolo de captura si es necesario
        if (isCapture) {
            move.append("x");
        }
        
        // Añadir coordenadas de destino
        move.append((char)('a' + toCol));
        move.append(8 - toRow);
        
        // Verificar si hay jaque
        boolean isCheck = isKingInCheck(currentRanks, currentParts[1].equals("w"));
        if (isCheck) {
            move.append("+");
        }
        
        return move.toString();
    }

    /**
     * Verifica si el rey está en jaque.
     * @param ranks Estado actual del tablero
     * @param isWhite true si es el rey blanco, false si es el negro
     * @return true si el rey está en jaque
     */
    private boolean isKingInCheck(String[] ranks, boolean isWhite) {
        // Encontrar la posición del rey
        int kingCol = -1, kingRow = -1;
        char kingPiece = isWhite ? 'K' : 'k';
        
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (getPieceAt(ranks, col, row) == kingPiece) {
                    kingCol = col;
                    kingRow = row;
                    break;
                }
            }
        }
        
        // Verificar si alguna pieza puede atacar al rey
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                char piece = getPieceAt(ranks, col, row);
                if (piece != '1' && Character.isUpperCase(piece) != isWhite) {
                    // Verificar si la pieza puede atacar al rey
                    if (canAttackKing(piece, col, row, kingCol, kingRow, ranks)) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }

    /**
     * Verifica si una pieza puede atacar al rey.
     * @param piece Carácter de la pieza
     * @param fromCol Columna de la pieza
     * @param fromRow Fila de la pieza
     * @param kingCol Columna del rey
     * @param kingRow Fila del rey
     * @param ranks Estado actual del tablero
     * @return true si la pieza puede atacar al rey
     */
    private boolean canAttackKing(char piece, int fromCol, int fromRow, int kingCol, int kingRow, String[] ranks) {
        char upperPiece = Character.toUpperCase(piece);
        
        // Verificar según el tipo de pieza
        switch (upperPiece) {
            case 'P': // Peón
                int direction = Character.isUpperCase(piece) ? -1 : 1;
                return Math.abs(fromCol - kingCol) == 1 && (fromRow - kingRow) == direction;
                
            case 'R': // Torre
                return fromCol == kingCol || fromRow == kingRow;
                
            case 'N': // Caballo
                int colDiff = Math.abs(fromCol - kingCol);
                int rowDiff = Math.abs(fromRow - kingRow);
                return (colDiff == 2 && rowDiff == 1) || (colDiff == 1 && rowDiff == 2);
                
            case 'B': // Alfil
                return Math.abs(fromCol - kingCol) == Math.abs(fromRow - kingRow);
                
            case 'Q': // Reina
                return fromCol == kingCol || fromRow == kingRow || 
                       Math.abs(fromCol - kingCol) == Math.abs(fromRow - kingRow);
                
            case 'K': // Rey
                return Math.abs(fromCol - kingCol) <= 1 && Math.abs(fromRow - kingRow) <= 1;
                
            default:
                return false;
        }
    }

    /**
     * Obtiene la pieza en una posición específica del tablero.
     * @param ranks Array de strings representando las filas del tablero
     * @param col Columna (0-7)
     * @param row Fila (0-7)
     * @return Carácter representando la pieza o '1' para casilla vacía
     */
    private char getPieceAt(String[] ranks, int col, int row) {
        String rank = ranks[7 - row];
        int currentCol = 0;
        
        for (char c : rank.toCharArray()) {
            if (Character.isDigit(c)) {
                int spaces = Character.getNumericValue(c);
                if (currentCol + spaces > col) {
                    return '1';
                }
                currentCol += spaces;
            } else {
                if (currentCol == col) {
                    return c;
                }
                currentCol++;
            }
        }
        return '1';
    }
}