package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.IOException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import utils.FontLoader;

/**
 * Panel que muestra el historial de movimientos del juego de ajedrez.
 * Utiliza notación algebraica para representar los movimientos.
 */
public class MoveHistoryPanel extends JPanel {
    private JTable moveHistoryTable;
    private JScrollPane scrollPane;
    private DefaultTableModel tableModel;
    
    // Constantes para el estilo
    private static final Color BACKGROUND_COLOR = new Color(41, 41, 50);
    private static final Color PANEL_COLOR = new Color(51, 51, 60);
    private static final Color BORDER_COLOR = new Color(51, 51, 60);
    private static final Color TEXT_COLOR = new Color(250, 255, 224);
    private static final Color SCROLLBAR_COLOR = new Color(61, 61, 70);
    private static final Color SCROLLBAR_THUMB_COLOR = new Color(81, 81, 90);
    private static final Color TABLE_GRID_COLOR = new Color(71, 71, 80);
    
    // Fuentes personalizadas
    private static Font TITLE_FONT;
    private static Font TABLE_FONT;
    
    static {
        try {
            TITLE_FONT = FontLoader.loadFont(26f);
            TABLE_FONT = FontLoader.loadFont(24f);
        } catch (IOException e) {
            // Si hay error, usar fuentes por defecto
            TITLE_FONT = new Font("Century Gothic", Font.BOLD, 26);
            TABLE_FONT = new Font("Century Gothic", Font.PLAIN, 24);
        }
    }
    
    private static final int PADDING = 10;

    /**
     * Constructor que inicializa el panel de historial de movimientos.
     * Configura el diseño y los componentes visuales.
     */
    public MoveHistoryPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(200, 400));
        setBackground(BACKGROUND_COLOR);
        
        // Crear el modelo de tabla
        tableModel = new DefaultTableModel(new String[]{"#", "W", "B"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Crear la tabla
        moveHistoryTable = new JTable(tableModel);
        moveHistoryTable.setFont(TABLE_FONT);
        moveHistoryTable.setForeground(TEXT_COLOR);
        moveHistoryTable.setBackground(PANEL_COLOR);
        moveHistoryTable.setGridColor(TABLE_GRID_COLOR);
        moveHistoryTable.setShowGrid(true);
        moveHistoryTable.setRowHeight(35);
        
        // Alinear la columna de números a la derecha
        moveHistoryTable.getColumnModel().getColumn(0).setHeaderRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            {
                setHorizontalAlignment(SwingConstants.RIGHT);
            }
        });
        moveHistoryTable.getColumnModel().getColumn(0).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            {
                setHorizontalAlignment(SwingConstants.RIGHT);
            }
        });
        
        // Personalizar el encabezado de la tabla
        JTableHeader header = moveHistoryTable.getTableHeader();
        header.setFont(TABLE_FONT);
        header.setBackground(PANEL_COLOR);
        header.setForeground(TEXT_COLOR);
        header.setBorder(BorderFactory.createLineBorder(TABLE_GRID_COLOR));
        
        // Ajustar el ancho de las columnas
        TableColumnModel columnModel = moveHistoryTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(40);  // Columna de número
        columnModel.getColumn(1).setPreferredWidth(80);  // Columna de blancas
        columnModel.getColumn(2).setPreferredWidth(80);  // Columna de negras
        
        // Configurar el scroll pane
        scrollPane = new JScrollPane(moveHistoryTable);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(BACKGROUND_COLOR);
        
        // Personalizar la barra de desplazamiento
        scrollPane.getVerticalScrollBar().setBackground(SCROLLBAR_COLOR);
        scrollPane.getVerticalScrollBar().setForeground(SCROLLBAR_THUMB_COLOR);
        scrollPane.getVerticalScrollBar().setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(12, 0));
        
        // Personalizar el viewport
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        
        // Título con estilo mejorado
        JLabel title = new JLabel("Move History", SwingConstants.CENTER);
        title.setFont(TITLE_FONT);
        title.setForeground(TEXT_COLOR);
        title.setBorder(BorderFactory.createEmptyBorder(PADDING, 0, PADDING, 0));
        
        add(title, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Actualiza el historial de movimientos con la nueva lista de FENs.
     * @param fenHistory Lista de strings FEN que representan el estado del tablero en cada movimiento
     */
    public void updateMoveHistory(List<String> fenHistory) {
        // Limpiar la tabla
        tableModel.setRowCount(0);
        
        // Procesar los movimientos
        for (int i = 1; i < fenHistory.size(); i += 2) {
            String whiteMove = convertFENtoMove(fenHistory.get(i-1), fenHistory.get(i));
            String blackMove = (i + 1 < fenHistory.size()) ? 
                             convertFENtoMove(fenHistory.get(i), fenHistory.get(i+1)) : "";
            
            tableModel.addRow(new Object[]{
                (i/2 + 1) + ".",
                whiteMove,
                blackMove
            });
        }
        
        // Auto-scroll al final
        if (moveHistoryTable.getRowCount() > 0) {
            moveHistoryTable.scrollRectToVisible(
                moveHistoryTable.getCellRect(moveHistoryTable.getRowCount() - 1, 0, true)
            );
        }
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