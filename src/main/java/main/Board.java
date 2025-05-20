package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import pieces.Piece;
import pieces.Queen;
import sfx.SFXManager;

public class Board extends JPanel {
    // Board dimensions and properties
    public int tileSize = 65;
    public int cols = 8;
    public int rows = 8;
    public int boardSize = 8; // Usado por CheckScanner base
    
    // Game state tracking
    public ArrayList<Piece> pieceList = new ArrayList<>();
    public Piece selectedPiece;
    public boolean isWhiteToMove = true;
    public boolean isGameOver = false;
    public boolean wasInCheckLastTurn = false;
    public boolean isInCheck = false;
    
    // Castling rights
    public boolean whiteCanCastleKingside = true;
    public boolean whiteCanCastleQueenside = true;
    public boolean blackCanCastleKingside = true;
    public boolean blackCanCastleQueenside = true;
    
    // Move counters
    public int halfmoveClock = 0;
    public int fullmoveNumber = 1;
    
    // Special moves
    public int enPassantTile = -1;
    
    // Game components
    public Input input = new Input(this);
    public CheckScanner checkScanner = new CheckScanner(this);
    public FEN fenHandler;
    private SFXManager sfx;
    
    // Game history
    private List<String> gameHistory = new ArrayList<>();
    private static final String STARTING_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    public MoveHistoryPanel moveHistoryPanel;
    
    public Board() {
        this.setPreferredSize(new Dimension(cols * tileSize, rows * tileSize));
        this.addMouseListener(input);
        this.addMouseMotionListener(input);
        this.sfx = SFXManager.getInstance();
        this.fenHandler = new FEN(this);
        this.moveHistoryPanel = new MoveHistoryPanel();
        loadFromFEN(STARTING_FEN);
        printCurrentTurn();
        checkForCheck();
        gameHistory.add(generateFEN());
        
        // Update move history panel (should be empty for a new game)
        // Assuming moveHistoryPanel is not null and has an update method that handles empty history
        if (moveHistoryPanel != null) {
            moveHistoryPanel.updateMoveHistory(gameHistory, false, isInCheck); // Pass checkmate (false) and isInCheck (initial state)
        }
    }

    public MoveHistoryPanel getMoveHistoryPanel() {
       return moveHistoryPanel;
    }   
    
    public void makeMove(Move move) {
        Piece currentKing = findKing(isWhiteToMove);
        boolean wasInCheckBeforeMove = isInCheck;
        
        if (move.piece.name.equals("Pawn") || move.capture != null) {
            halfmoveClock = 0;
        } else {
            halfmoveClock++;
        }
        
        updateCastlingRights(move);
        
        if (move.piece.name.equals("Pawn")) {
            movePawn(move);
        } else if (move.piece.name.equals("King")) {
            moveKing(move);
        }
        
        move.piece.col = move.newCol;
        move.piece.row = move.newRow;
        move.piece.xPos = move.newCol * tileSize;
        move.piece.yPos = move.newRow * tileSize;
        move.piece.isFirstMove = false;
        
        capture(move.capture);
        
        isWhiteToMove = !isWhiteToMove;
        
        if (isWhiteToMove) {
            fullmoveNumber++;
        }
        
        gameHistory.add(generateFEN());
        printCurrentTurn();
        checkForCheck();
        boolean isCheckmate = updateGameState();
        
        // Temporary print statements for debugging castling notation
        if (gameHistory.size() >= 2) {
            String prevFEN = gameHistory.get(gameHistory.size() - 2);
            String currentFEN = gameHistory.get(gameHistory.size() - 1);
            System.out.println("[DEBUG] Move History FENs:");
            System.out.println("[DEBUG]   Prev FEN: " + prevFEN);
            System.out.println("[DEBUG]   Current FEN: " + currentFEN);
        }
        
        moveHistoryPanel.updateMoveHistory(gameHistory, isCheckmate, isInCheck);
    }

    private void updateCastlingRights(Move move) {
        if (move.piece.name.equals("King")) {
            if (move.piece.isWhite) {
                whiteCanCastleKingside = false;
                whiteCanCastleQueenside = false;
            } else {
                blackCanCastleKingside = false;
                blackCanCastleQueenside = false;
            }
        }
        
        if (move.piece.name.equals("Rook") || (move.capture != null && move.capture.name.equals("Rook"))) {
            Piece rook = move.piece.name.equals("Rook") ? move.piece : move.capture;
            
            if (rook.isWhite) {
                if (rook.col == 7 && rook.row == 7) whiteCanCastleKingside = false;
                if (rook.col == 0 && rook.row == 7) whiteCanCastleQueenside = false;
            } else {
                if (rook.col == 7 && rook.row == 0) blackCanCastleKingside = false;
                if (rook.col == 0 && rook.row == 0) blackCanCastleQueenside = false;
            }
        }
    }

    private void moveKing(Move move) {
        if (Math.abs(move.piece.col - move.newCol) == 2) {
            Piece rook;
            
            if (move.piece.col < move.newCol) {
                rook = getPiece(7, move.piece.row);
                rook.col = 5;
                System.out.println((move.piece.isWhite ? "White" : "Black") + " castles kingside!");
            } else {
                rook = getPiece(0, move.piece.row);
                rook.col = 3;
                System.out.println((move.piece.isWhite ? "White" : "Black") + " castles queenside!");
            }
            rook.xPos = rook.col * tileSize;
        }
    }

    private void movePawn(Move move) {
        int colorIndex = move.piece.isWhite ? 1 : -1;
        
        if (getTileNum(move.newCol, move.newRow) == enPassantTile) {
            move.capture = getPiece(move.newCol, move.newRow + colorIndex);
            System.out.println((move.piece.isWhite ? "White" : "Black") + " captures en passant!");
        }
        
        if (Math.abs(move.piece.row - move.newRow) == 2) {
            enPassantTile = getTileNum(move.newCol, move.newRow + colorIndex);
        } else {
            enPassantTile = -1;
        }
        
        int promotionRow = move.piece.isWhite ? 0 : 7;
        if (move.newRow == promotionRow) {
            promotePawn(move);
        }
    }

    private void promotePawn(Move move) {
        pieceList.add(new Queen(this, move.newCol, move.newRow, move.piece.isWhite));
        capture(move.piece);
        System.out.println((move.piece.isWhite ? "White" : "Black") + " pawn promotes to Queen!");
    }

    public void capture(Piece piece) {
        if (piece != null) {
            pieceList.remove(piece);
            System.out.println((piece.isWhite ? "White" : "Black") + " " + piece.name + " captured!");
            sfx.playSound("delete");
        } else {
            sfx.playSound("put");
        }
    }

    /**
     * Verifica si un movimiento es legal según las reglas de ajedrez.
     * - No permite mover si el juego terminó.
     * - Solo permite mover piezas del color que tiene el turno.
     * - No permite capturar piezas propias.
     * - Verifica que el movimiento sea válido para la pieza.
     * - Verifica que no haya colisiones en la trayectoria (excepto caballos).
     * - No permite movimientos que dejen al propio rey en jaque.
     */
    public boolean isValidMove(Move move) {
        if (isGameOver) return false; // El juego terminó
        if (move.piece == null) return false; // No hay pieza para mover
        if (move.piece.isWhite != isWhiteToMove) return false; // No es el turno de este color
        if (sameTeam(move.piece, move.capture)) return false; // No puedes capturar tus propias piezas

        // Reglas básicas de movimiento de la pieza y colisiones
        // Nota: Estos métodos (isValidMovement, moveCollidesWithPiece) deben estar implementados
        // en las clases Piece y sus subclases, o Board debe tener la lógica de validación para cada tipo de pieza.
        // Asumiendo que existen y funcionan correctamente para el tipo de pieza.
        if (!move.piece.isValidMovement(move.newCol, move.newRow)) {
             // System.out.println("[DEBUG] isValidMove: Invalid movement pattern for " + move.piece.name);
            return false; // Movimiento no válido para el tipo de pieza
        }

         // Verificar colisiones en la trayectoria (excepto caballos)
        if (move.piece.name != null && !move.piece.name.equals("Knight")) {
            if (move.piece.moveCollidesWithPiece(move.newCol, move.newRow)) {
                // System.out.println("[DEBUG] isValidMove: Move collides with piece.");
                return false; // Hay piezas bloqueando
            }
        }

        // --- Lógica para simular el movimiento y verificar si deja al rey propio en jaque ---
        // No modificamos directamente la lista de piezas del Board aquí.
        // Solo simulamos temporalmente el estado para la verificación del jaque.

        // Guardar el estado original de la pieza que se mueve
        int originalCol = move.piece.col;
        int originalRow = move.piece.row;

        // Guardar la pieza capturada (si existe) para restaurarla después
        Piece capturedPiece = getPiece(move.newCol, move.newRow);

        // --- Simular el movimiento temporalmente para la verificación de jaque ---
        // Actualizar la posición de la pieza en el objeto move.piece.
        // Esto es importante porque checkScanner.isKingChecked llamará a board.getPiece,
        // y getPiece necesita las coordenadas actuales (simuladas) de la pieza.
        move.piece.col = move.newCol;
        move.piece.row = move.newRow;

        // Si la pieza capturada existe, la removemos TEMPORALMENTE para la verificación de jaque.
        if (capturedPiece != null) {
            pieceList.remove(capturedPiece);
        }

         // Para el caso especial de enroque, simular también el movimiento temporal de la torre
        Piece movedRook = null;
        int originalRookCol = -1;
        int originalRookRow = -1;

        // Verificar si el movimiento propuesto es un enroque (rey mueve dos casillas)
        // Usamos originalCol para comparar con la columna de origen del rey antes de la simulación
        if (move.piece.name.equals("King") && Math.abs(originalCol - move.newCol) == 2) {
             // Determinar qué torre se mueve en el enroque simulado
            if (move.newCol == 6) { // Enroque corto (Rey va a g1/g8)
                movedRook = getPiece(7, originalRow); // Torre en h1 o h8
                 if (movedRook != null) {
                     originalRookCol = movedRook.col;
                     originalRookRow = movedRook.row;
                     movedRook.col = 5; // Simular movimiento de torre a f1/f8
                 }
            } else if (move.newCol == 2) { // Enroque largo (Rey va a c1/c8)
                movedRook = getPiece(0, originalRow); // Torre en a1 o a8
                 if (movedRook != null) {
                     originalRookCol = movedRook.col;
                     originalRookRow = movedRook.row;
                     movedRook.col = 3; // Simular movimiento de torre a d1/d8
                 }
            }
             // Nota: En el enroque, no hay pieza capturada en el destino del rey o la torre.
        }


        // Encontrar la posición del rey del color que se mueve en el estado SIMULADO.
        // Si la pieza que se mueve es el rey, su nueva posición es move.newCol/Row.
        // Si se mueve otra pieza, la posición del rey propio no cambia, pero debemos
        // obtener la instancia del rey de la lista de piezas (que ya refleja el estado simulado si se movió el rey).
        Piece kingOfMovingColor = findKing(move.piece.isWhite);
        // Las coordenadas del rey ya están actualizadas si la pieza movida fue el rey
        int kingSimulatedCol = kingOfMovingColor.col;
        int kingSimulatedRow = kingOfMovingColor.row;


        // Verificar si el rey propio (del color que se mueve) está en jaque en el estado SIMULADO.
        // Llamamos a isKingChecked para la posición del rey en el estado simulado,
        // pidiendo a CheckScanner que busque atacantes del color OPUESTO (!move.piece.isWhite).
        boolean leavesKingInCheck = checkScanner.isKingChecked(
            kingSimulatedCol,     // Columna del rey en el estado simulado
            kingSimulatedRow,     // Fila del rey en el estado simulado
            move.piece.isWhite    // Le decimos a isKingChecked el color del rey que verificamos.
                                  // CheckScanner buscará automáticamente atacantes del color opuesto.
        );

        // --- Restaurar el estado original del tablero después de la verificación ---

        // Restaurar la posición original de la pieza que se movió
        move.piece.col = originalCol;
        move.piece.row = originalRow;

         // Restaurar la pieza capturada a la lista principal si existía
        if (capturedPiece != null) {
            // Re-añadir la pieza capturada a la lista en su posición original
            pieceList.add(capturedPiece);
        }

        // Restaurar la posición original de la torre si fue un enroque simulado
        if(movedRook != null) {
            movedRook.col = originalRookCol;
            movedRook.row = originalRookRow; // Asegurarse de restaurar también la fila si es necesario, aunque en enroque la fila es la misma.
        }

        // Si el movimiento simulado deja al rey propio en jaque, el movimiento original es inválido.
        if (leavesKingInCheck) {
            // System.out.println("[DEBUG] isValidMove: Move to (" + move.newCol + "," + move.newRow + ") leaves king in check.");
            return false;
        }

        // Si pasa todas las verificaciones, el movimiento es válido
        // System.out.println("[DEBUG] isValidMove: Move to (" + move.newCol + "," + move.newRow + ") is valid.");
        return true;
    }

    public boolean sameTeam(Piece p1, Piece p2) {
        if (p1 == null || p2 == null) return false;
        return p1.isWhite == p2.isWhite;
    }

    public Piece getPiece(int col, int row) {
        if (col < 0 || col >= cols || row < 0 || row >= rows) return null;
        
        for (Piece piece : pieceList) {
            if (piece.col == col && piece.row == row) {
                return piece;
            }
        }
        return null;
    }

    public Piece findKing(boolean isWhite) {
        for (Piece piece : pieceList) {
            if (piece.isWhite == isWhite && piece.name.equals("King")) {
                return piece;
            }
        }
        return null;
    }

    public int getTileNum(int col, int row) {
        return row * rows + col;
    }

    public void loadFromFEN(String fen) {
        fenHandler.loadFromFEN(fen);
    }

    public String generateFEN() {
        return fenHandler.generateFEN();
    }

    /**
     * Resets the board to the starting game position (FEN).
     * Clears game history and resets game state variables.
     */
    public void loadBoard() {
        // Clear existing pieces
        pieceList.clear();

        // Reset game state variables
        isWhiteToMove = true;
        isGameOver = false;
        wasInCheckLastTurn = false;
        isInCheck = false;

        // Reset castling rights
        whiteCanCastleKingside = true;
        whiteCanCastleQueenside = true;
        blackCanCastleKingside = true;
        blackCanCastleQueenside = true;

        // Reset move counters
        halfmoveClock = 0;
        fullmoveNumber = 1;

        // Reset en passant tile
        enPassantTile = -1;

        // Clear game history
        gameHistory.clear();

        // Load the starting FEN
        loadFromFEN(STARTING_FEN);

        // Update move history panel (should be empty for a new game)
        // Assuming moveHistoryPanel is not null and has an update method that handles empty history
        if (moveHistoryPanel != null) {
            moveHistoryPanel.updateMoveHistory(gameHistory, false, isInCheck); // Pass false for isCheckmate and the current isInCheck state
        }

        // Optional: Reset SFX state if necessary (e.g., stop check music)
        // sfx.stopAllMusic(); // Or specific check music reset

        // Request repaint
        repaint();

        // Print initial turn
        printCurrentTurn();

        // Add initial FEN to history (for the first move number)
         gameHistory.add(generateFEN());

    }

    private void checkForCheck() {
        Piece king = findKing(isWhiteToMove);
        // Verificar jaque para el rey del turno actual en su posición actual
        boolean isCurrentlyInCheck = checkScanner.isKingChecked(
            king.col,
            king.row,
            isWhiteToMove
        );
        
        if (isCurrentlyInCheck != isInCheck) {
            isInCheck = isCurrentlyInCheck;
            if (isInCheck) {
                System.out.println("CHECK! " + (isWhiteToMove ? "White" : "Black") + " king is in check!");
                wasInCheckLastTurn = true;
            } else if (wasInCheckLastTurn) {
                System.out.println((isWhiteToMove ? "White" : "Black") + " king is no longer in check.");
                wasInCheckLastTurn = false;
            }
        }
    }

    public void setMoveHistoryPanel(MoveHistoryPanel panel) {
        this.moveHistoryPanel = panel;
    }
    
    /**
     * Actualiza el estado general del juego (jaque mate, tablas).
     * @return true si la partida terminó en jaque mate, false en caso contrario.
     */
    private boolean updateGameState() {
        if (isGameOver) return false; // El juego ya terminó
        
        boolean checkmate = checkScanner.isCheckmate(isWhiteToMove);
        boolean stalemate = checkScanner.isStalemate(isWhiteToMove);
        final String message;
        final String title;
        final int result;
        
        boolean partidaTerminadaPorJaqueMate = false;

        if (checkmate) {
            message = (isWhiteToMove ? "Black" : "White") + " wins by checkmate!";
            title = "Checkmate";
            sfx.playSound("checkmate");
            isGameOver = true;
            partidaTerminadaPorJaqueMate = true;
        } else if (stalemate) {
            message = "Draw by stalemate!";
            title = "Stalemate";
            sfx.playSound("stalemate");
            isGameOver = true;
        } else {
             // Inicializar si no hay jaque mate o tablas para evitar errores de "variable might not have been initialized"
            message = null;
            title = null;
            result = -1; // O algún valor por defecto apropiado
        }

        if (isGameOver) {
            // Mostrar OptionPane
            // Estilo OptionPane (como en MainMenu)
            // Ejecutar en un hilo aparte para no bloquear el EDT y permitir el repintado del tablero
            SwingUtilities.invokeLater(() -> {
                try {
                    javax.swing.UIManager.put("OptionPane.background", new Color(33, 27, 47));
                    javax.swing.UIManager.put("Panel.background", new Color(33, 27, 47));
                    javax.swing.UIManager.put("OptionPane.messageForeground", new Color(200, 160, 60));
                    javax.swing.UIManager.put("Button.background", new Color(70, 50, 20));
                    javax.swing.UIManager.put("Button.foreground", new Color(200, 160, 60));
                    javax.swing.UIManager.put("Button.font", utils.FontLoader.loadFont(24f));
                    // Añadir fuente para el mensaje
                    javax.swing.UIManager.put("OptionPane.messageFont", utils.FontLoader.loadFont(24f));
                } catch (Exception e) {
                    // fallback
                }
                Object[] options = {"Play Again", "Go to Menu"};
                int dialogResult = javax.swing.JOptionPane.showOptionDialog(
                    this,
                    message,
                    title,
                    javax.swing.JOptionPane.DEFAULT_OPTION,
                    javax.swing.JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]
                );
                // Restaurar UIManager
                javax.swing.UIManager.put("OptionPane.background", null);
                javax.swing.UIManager.put("Panel.background", null);
                javax.swing.UIManager.put("OptionPane.messageForeground", null);
                javax.swing.UIManager.put("Button.background", null);
                javax.swing.UIManager.put("Button.foreground", null);
                javax.swing.UIManager.put("Button.font", null);
                // Acción según opción
                java.awt.Container parent = this.getParent();
                while (parent != null && !(parent instanceof javax.swing.JFrame)) {
                    parent = parent.getParent();
                }
                if (dialogResult == 0) { // Play Again
                    if (parent != null) {
                        if (parent instanceof javax.swing.JFrame) {
                            for (java.awt.Component comp : ((javax.swing.JFrame)parent).getContentPane().getComponents()) {
                                if (comp instanceof screens.Game) {
                                    ((screens.Game)comp).resetGame();
                                    break;
                                }
                            }
                        }
                    }
                } else if (dialogResult == 1) { // Go to Menu
                    if (parent != null) {
                        if (parent instanceof javax.swing.JFrame) {
                            for (java.awt.Component comp : ((javax.swing.JFrame)parent).getContentPane().getComponents()) {
                                if (comp instanceof javax.swing.JPanel) {
                                    java.awt.CardLayout cl = (java.awt.CardLayout)((javax.swing.JPanel)comp).getLayout();
                                    cl.show((javax.swing.JPanel)comp, "Menu");
                                    break;
                                }
                            }
                        }
                    }
                }
            });
        }
        
        return partidaTerminadaPorJaqueMate;
    }

    private void printCurrentTurn() {
        System.out.println("\n" + "=".repeat(30));
        System.out.println("Turn: " + (isWhiteToMove ? "WHITE" : "BLACK") + " to move");
        System.out.println("FEN: " + generateFEN());
        System.out.println("=".repeat(30));
    }

    // este método regresa el turno en forma de String
    public String getTurn() {
        return (isWhiteToMove ? "White" : "Black")+"'s Turn";
    }
    
    public List<String> getGameHistory() {
        return new ArrayList<>(gameHistory);
    }

    public void printGameHistory() {
        System.out.println("\n=== GAME HISTORY ===");
        for (int i = 0; i < gameHistory.size(); i++) {
            System.out.println("Move " + i + ": " + gameHistory.get(i));
        }
        System.out.println("====================");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Draw board
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                g2d.setColor((c + r) % 2 == 0 ? new Color(76, 76, 100) : new Color(200, 196, 194));
                g2d.fillRect(c * tileSize, r * tileSize, tileSize, tileSize);
            }
        }

        // Resaltar el rey en jaque
        if (isInCheck) {
            Piece king = findKing(isWhiteToMove);
            if (king != null) {
                g2d.setColor(new Color(220, 40, 40, 180)); // Rojo semitransparente
                int x = king.col * tileSize;
                int y = king.row * tileSize;
                g2d.fillOval(x + tileSize/8, y + tileSize/8, tileSize*3/4, tileSize*3/4);
            }
        }
        
        // Draw possible moves
        if (selectedPiece != null) {
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    if (isValidMove(new Move(this, selectedPiece, c, r))) {
                        g2d.setColor(new Color(211, 255, 109, 170));
                        g2d.fillRect(c * tileSize, r * tileSize, tileSize, tileSize);
                    }
                }
            }
        }
        
        // Draw pieces
        for (Piece piece : pieceList) {
            piece.paint(g2d);
        }
    }
}