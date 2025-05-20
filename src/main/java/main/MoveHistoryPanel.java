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

import sfx.SFXManager;
import utils.FontLoader;

/**
 * Panel que muestra el historial de movimientos del juego de ajedrez.
 * Utiliza notación algebraica para representar los movimientos.
 */
public class MoveHistoryPanel extends JPanel {
    private JTable moveHistoryTable;
    private JScrollPane scrollPane;
    private DefaultTableModel tableModel;
    private SFXManager sfx;
    private boolean lastCheckState = false;
    private FEN fenHandler;
    
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
        this.sfx = SFXManager.getInstance();
        this.fenHandler = new FEN(null); // No necesitamos el Board aquí
        
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
     * @param isCheckmate Indica si el último movimiento resultó en jaque mate
     * @param isInCheck Indica si el rey del color cuyo turno acaba de terminar está en jaque
     */
    public void updateMoveHistory(List<String> fenHistory, boolean isCheckmate, boolean isInCheck) {
        // Limpiar la tabla
        tableModel.setRowCount(0);
        
        // Procesar los movimientos
        for (int i = 1; i < fenHistory.size(); i += 2) {
            String whiteMove = fenHandler.convertFENtoMove(fenHistory.get(i-1), fenHistory.get(i));
            String blackMove = (i + 1 < fenHistory.size()) ? 
                             fenHandler.convertFENtoMove(fenHistory.get(i), fenHistory.get(i+1)) : "";
            
            // Temporary print statements for debugging castling notation in MoveHistoryPanel
            System.out.println("[DEBUG] MoveHistoryPanel processing move pair:");
            System.out.println("[DEBUG]   White Move String: " + whiteMove);
            System.out.println("[DEBUG]   Black Move String: " + blackMove);
            
            // Si es el último movimiento y es jaque mate, añadir #
            if (i + 1 >= fenHistory.size()) {
                 if (isCheckmate) {
                     if (!blackMove.isEmpty()) {
                    blackMove += "#";
                 } else {
                    whiteMove += "#";
                 }
                 } else if (isInCheck) {
                     if (!blackMove.isEmpty()) {
                        blackMove += "+";
                    } else {
                        whiteMove += "+";
                    }
                 }
            }

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
}