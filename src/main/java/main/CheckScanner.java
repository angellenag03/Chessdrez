package main;

import java.util.Iterator;
import pieces.Piece;

/**
 * Clase para escanear el tablero y determinar situaciones de jaque, jaque mate y tablas.
 * Adaptado de la lógica del proyecto base.
 */
public class CheckScanner {

    Board board;

    public CheckScanner (Board board) {
        this.board = board;
    }

    /**
     * Verifica si una casilla específica está bajo ataque por piezas del color opuesto al especificado.
     * @param col Columna de la casilla a verificar.
     * @param row Fila de la casilla a verificar.
     * @param isWhite True si el rey siendo verificado es blanco (buscamos atacantes negros), False si es negro (buscamos atacantes blancos).
     * @return True si la casilla está bajo ataque por el color opuesto, False en caso contrario.
     */
    public boolean isKingChecked(int col, int row, boolean isWhite) {
        Piece king = board.findKing(isWhite);
        // assert king != null; // Se eliminó el assert para evitar posibles errores en tiempo de ejecución si findKing retorna null

        if (king == null) return false; // Si no hay rey, no puede haber jaque

        // sets kingCol/Row to kings location on board
        int kingCol = king.col;
        int kingRow = king.row;
        // Corregido: usar comparación de String y el nombre correcto del Rey
        if (board.selectedPiece != null && board.selectedPiece.name.equals("King")) {
            kingCol = col;
            kingRow = row;
        }

        // Adaptado: los métodos scan ahora solo necesitan la casilla objetivo y el color del rey
        return ScanStraight(col, row, isWhite) ||    // up, right, down, left (Torre y Dama)
               scanDiagonally(col, row, isWhite) ||   // diagonales (Alfil y Dama)
               scanForKnight(col, row, isWhite) ||  // Caballo
               scanForKing(col, row, isWhite) ||    // Rey (para evitar que se pongan adyacentes)
               scanForPawn(col, row, isWhite);     // Peón
    }

    /**
     * Escanea en líneas rectas (horizontal y vertical) desde la casilla objetivo buscando atacantes.
     * @param targetCol Columna objetivo.
     * @param targetRow Fila objetivo.
     * @param isKingWhite True si el rey siendo verificado es blanco (buscamos atacantes negros), False si es negro (buscamos atacantes blancos).
     * @return True si se encuentra una Torre o Dama atacante, False en caso contrario.
     */
    private boolean ScanStraight(int targetCol, int targetRow, boolean isKingWhite) {
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

        for (int[] dir : directions) {
            int col = targetCol + dir[0];
            int row = targetRow + dir[1];

            // Corregido: Usar 8 para el tamaño del tablero en lugar de board.boardSize
            while (col >= 0 && col < 8 && row >= 0 && row < 8) {
                Piece p = board.getPiece(col, row);

                if (p != null) {
                    // Corregido: Usar comparación de String para los nombres de las piezas
                    // Buscamos piezas enemigas (color opuesto al rey) que sean Torres o Damas
                    if (p.isWhite != isKingWhite && (p.name.equals("Rook") || p.name.equals("Queen"))) {
                        return true;
                    }
                    break; // La línea está bloqueada por otra pieza
                }

                col += dir[0];
                row += dir[1];
            }
        }
        return false;
    }

    /**
     * Escanea en diagonales desde la casilla objetivo buscando atacantes.
     * @param targetCol Columna objetivo.
     * @param targetRow Fila objetivo.
     * @param isKingWhite True si el rey siendo verificado es blanco (buscamos atacantes negros), False si es negro (buscamos atacantes blancos).
     * @return True si se encuentra un Alfil o Dama atacante, False en caso contrario.
     */
    private boolean scanDiagonally(int targetCol, int targetRow, boolean isKingWhite) {
        int[][] directions = {{-1, -1}, {1, -1}, {1, 1}, {-1, 1}};

         for (int[] dir : directions) {
            int col = targetCol + dir[0];
            int row = targetRow + dir[1];

            // Corregido: Usar 8 para el tamaño del tablero
            while (col >= 0 && col < 8 && row >= 0 && row < 8) {
                Piece p = board.getPiece(col, row);

                if (p != null) {
                    // Corregido: Usar comparación de String
                     // Buscamos piezas enemigas (color opuesto al rey) que sean Alfiles o Damas
                    if (p.isWhite != isKingWhite && (p.name.equals("Bishop") || p.name.equals("Queen"))) {
                        return true;
                    }
                    break; // La línea está bloqueada por otra pieza
                }

                col += dir[0];
                row += dir[1];
            }
        }
        return false;
    }

    /**
     * Verifica si un Caballo atacante amenaza la casilla objetivo.
     * @param targetCol Columna objetivo.
     * @param targetRow Fila objetivo.
     * @param isKingWhite True si el rey siendo verificado es blanco (buscamos atacantes negros), False si es negro (buscamos atacantes blancos).
     * @return True si se encuentra un Caballo atacante, False en caso contrario.
     */
    private boolean scanForKnight(int targetCol, int targetRow, boolean isKingWhite) {
        int[][] knightMoves = {
            {-1, -2}, {1, -2}, {2, -1}, {2, 1},
            {1, 2}, {-1, 2}, {-2, 1}, {-2, -1}
        };

        for (int[] move : knightMoves) {
            int col = targetCol + move[0];
            int row = targetRow + move[1];

            if (col >= 0 && col < 8 && row >= 0 && row < 8) {
                Piece p = board.getPiece(col, row);
                 // Corregido: Usar comparación de String
                 // Buscamos Caballo enemigo
                if (p != null && p.isWhite != isKingWhite && p.name.equals("Knight")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Verifica si un Rey enemigo amenaza la casilla objetivo.
     * Se usa para evitar que los reyes se pongan adyacentes.
     * @param targetCol Columna objetivo.
     * @param targetRow Fila objetivo.
     * @param isKingWhite True si el rey siendo verificado es blanco (buscamos atacantes negros), False si es negro (buscamos atacantes blancos).
     * @return True si se encuentra un Rey atacante, False en caso contrario.
     */
    private boolean scanForKing(int targetCol, int targetRow, boolean isKingWhite) {
        // Un rey solo ataca casillas adyacentes
        int[][] kingMoves = {
             {-1, -1}, {0, -1}, {1, -1},
             {-1, 0},           {1, 0},
             {-1, 1}, {0, 1}, {1, 1}
        };

        for (int[] move : kingMoves) {
            int col = targetCol + move[0];
            int row = targetRow + move[1];

            if (col >= 0 && col < 8 && row >= 0 && row < 8) {
                 Piece p = board.getPiece(col, row);
                 // Corregido: Usar comparación de String
                 // Buscamos Rey enemigo
                 if (p != null && p.isWhite != isKingWhite && p.name.equals("King")) {
                     return true;
                 }
            }
        }
        return false;
    }

    /**
     * Verifica si un Peón atacante amenaza la casilla objetivo.
     * @param targetCol Columna objetivo.
     * @param targetRow Fila objetivo.
     * @param isKingWhite True si el rey siendo verificado es blanco (buscamos atacantes negros), False si es negro (buscamos atacantes blancos).
     * @return True si se encuentra un Peón atacante, False en caso contrario.
     */
    private boolean scanForPawn(int targetCol, int targetRow, boolean isKingWhite) {
        // Dirección desde la perspectiva del peón atacante. Peones blancos atacan 'arriba' (-1), negros 'abajo' (+1).
        // Si el rey es blanco (isKingWhite == true), buscamos peones negros que atacan hacia abajo (+1)
        // Si el rey es negro (isKingWhite == false), buscamos peones blancos que atacan hacia arriba (-1)
        int pawnDirection = isKingWhite ? 1 : -1;

        // Las casillas atacadas por un peón son las diagonales delanteras desde su perspectiva.
        // Por lo tanto, si una casilla (targetCol, targetRow) está atacada por un peón,
        // el peón debe estar en una fila 'atras' (respecto a su movimiento) y en una columna diagonal.

        int potentialPawnRow = targetRow - pawnDirection; // Fila donde buscar el peón atacante

        // Verificar las dos columnas diagonales donde podría estar el peón atacante
        for (int colOffset : new int[]{-1, 1}) {
            int potentialPawnCol = targetCol + colOffset;

            if (potentialPawnCol >= 0 && potentialPawnCol < 8 && potentialPawnRow >= 0 && potentialPawnRow < 8) {
                Piece p = board.getPiece(potentialPawnCol, potentialPawnRow);
                // Corregido: Usar comparación de String
                // Buscamos Peón enemigo
                if (p != null && p.isWhite != isKingWhite && p.name.equals("Pawn")) {
                    // Lógica adicional para peones: un peón solo ataca diagonalmente. La lógica actual ya verifica esto por posición.
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Verifica si hay movimientos legales disponibles para el color especificado.
     * Usado para determinar jaque mate o tablas por ahogado.
     * @param isWhite True si verificamos movimientos legales para las blancas, False para las negras.
     * @return True si no hay movimientos legales, False si hay al menos uno.
     */
    public boolean noValidMoves(boolean isWhite) {
        Iterator<Piece> iterator = board.pieceList.iterator();
        while (iterator.hasNext()) {
            Piece piece = iterator.next();
            if (piece.isWhite == isWhite) {
                for (int row = 0; row < board.rows; row++) {
                    for (int col = 0; col < board.cols; col++) {
                        Move potentialMove = new Move(board, piece, col, row);
                        if (board.isValidMove(potentialMove)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
    /**
     * Verifica si la partida ha terminado en jaque mate para el color especificado.
     * @param isWhite True si verificamos jaque mate para las blancas, False para las negras.
     * @return True si es jaque mate, False en caso contrario.
     */
    public boolean isCheckmate(boolean isWhite) {
         Piece king = board.findKing(isWhite);
         if (king == null) return false; // No hay rey, no puede haber jaque mate

         // Para que sea jaque mate, el rey debe estar en jaque Y no tener movimientos legales.
         // isKingChecked(rey.col, rey.row, isWhite) verifica si el rey en su posición actual está bajo ataque por el color opuesto.
         if (!isKingChecked(king.col, king.row, isWhite)) return false; // No está en jaque, no es jaque mate

         // Si el rey está en jaque, verificar si hay algún movimiento legal que lo salve.
         return noValidMoves(isWhite); // Si no hay movimientos legales, es jaque mate.
    }

    /**
     * Verifica si la partida ha terminado en tablas por ahogado para el color especificado.
     * @param isWhite True si verificamos tablas para las blancas, False para las negras.
     * @return True si es tablas por ahogado, False en caso contrario.
     */
    public boolean isStalemate(boolean isWhite) {
         Piece king = board.findKing(isWhite);
         if (king == null) return false; // No hay rey

         // Para que sea tablas por ahogado, el rey NO debe estar en jaque Y no tener movimientos legales.
         // isKingChecked(rey.col, rey.row, isWhite) verifica si el rey en su posición actual está bajo ataque por el color opuesto.
         if (isKingChecked(king.col, king.row, isWhite)) return false; // Está en jaque, no es tablas por ahogado

         // Si el rey no está en jaque, verificar si hay algún movimiento legal.
         return noValidMoves(isWhite); // Si no hay movimientos legales y no está en jaque, es tablas por ahogado.
    }

    // Método para verificar si el juego ha terminado (jaque mate o tablas)
    public boolean isGameOver() {
        return isCheckmate(board.isWhiteToMove) || isStalemate(board.isWhiteToMove);
    }
}

