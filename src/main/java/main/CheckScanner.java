package main;

import pieces.Piece;
import sfx.SFXManager;

/**
 *
 * @author pausa
 */
public class CheckScanner {
    Board board;
    private boolean lastCheckState = false;
    
    public CheckScanner(Board board) {
        this.board = board;
    }
    
    public boolean isKingChecked(Move move) {
        Piece king = board.findKing(move.piece.isWhite);
        assert king != null;
        
        int kingCol = king.col;
        int kingRow = king.row;
        if (board.selectedPiece != null && board.selectedPiece.name.equals("King")) {
            kingCol = move.newCol;
            kingRow = move.newRow;
        }
        
        boolean currentCheckState = 
                hitByRook  (move.newCol, move.newRow, king, kingCol, kingRow, 0, 1) || // up
                hitByRook  (move.newCol, move.newRow, king, kingCol, kingRow, 1, 0) || // right
                hitByRook  (move.newCol, move.newRow, king, kingCol, kingRow, 0, -1) || // down
                hitByRook  (move.newCol, move.newRow, king, kingCol, kingRow, -1, 0) || // left
                
                hitByBishop(move.newCol, move.newRow, king, kingCol, kingRow, -1, -1) || // upleft
                hitByBishop(move.newCol, move.newRow, king, kingCol, kingRow, 1, -1) || // upright
                hitByBishop(move.newCol, move.newRow, king, kingCol, kingRow, 1, 1) || // downright
                hitByBishop(move.newCol, move.newRow, king, kingCol, kingRow, -1, 1) || // downleft
                
                hitByKnight(move.newCol, move.newRow, king, kingCol, kingRow) ||
                hitByPawn(move.newCol, move.newRow, king, kingCol, kingRow) ||
                hitByKing(king, kingCol, kingRow);
        
        // Actualizar música solo si el estado de jaque cambió
        if (currentCheckState != lastCheckState) {
            SFXManager.getInstance().updateCheckState(currentCheckState);
            lastCheckState = currentCheckState;
        }
        
        return currentCheckState;
    }
    
    private boolean hitByRook(int col, int row, 
            Piece king, int kingCol, int kingRow, int colVal, int rowVal) 
    {
        for (int i = 1; i < 8; i++) {  // Empezar desde 1, no desde 0
            if (kingCol + (i * colVal) == col && kingRow + (i * rowVal) == row)
                break;

            Piece piece = board.getPiece(kingCol + (i * colVal), kingRow + (i * rowVal));
            if (piece != null && piece != board.selectedPiece) {
                if (!board.sameTeam(piece, king) && (piece.name.equals("Rook") || piece.name.equals("Queen"))) {
                    return true;
                }
                break;
            }
        }
        return false;
    }
    
    private boolean hitByBishop(int col, int row, 
            Piece king, int kingCol, int kingRow, int colVal, int rowVal) 
    {
        for (int i = 1; i < 8; i++) {  // Empezar desde 1
            if (kingCol + (i * colVal) == col && kingRow + (i * rowVal) == row) 
                break;

            Piece piece = board.getPiece(kingCol + (i * colVal), kingRow + (i * rowVal));
            if (piece != null && piece != board.selectedPiece) {
                if (!board.sameTeam(piece, king) && (piece.name.equals("Bishop") || piece.name.equals("Queen")))
                    return true;
                break;
            }
        }
        return false;
    }
    
    private boolean hitByKnight(int col, int row, Piece king, int kingCol, int kingRow) {
        return checkKnight(board.getPiece(kingCol-1, kingRow-2), king, col, row) ||
                checkKnight(board.getPiece(kingCol+1, kingRow-2), king, col, row) ||
                checkKnight(board.getPiece(kingCol+2, kingRow-1), king, col, row) ||
                checkKnight(board.getPiece(kingCol+2, kingRow+1), king, col, row) ||
                checkKnight(board.getPiece(kingCol+1, kingRow+2), king, col, row) ||
                checkKnight(board.getPiece(kingCol-1, kingRow+2), king, col, row) ||
                checkKnight(board.getPiece(kingCol-2, kingRow+1), king, col, row) ||
                checkKnight(board.getPiece(kingCol-2, kingRow-1), king, col, row); 
                
    }
    
    private boolean checkKnight(Piece p, Piece k, int col, int row) {
        return p != null && !board.sameTeam(p, k) && 
                p.name.equals("Knight") && !(p.col == col && p.row == row);
    }
    
    private boolean hitByKing(Piece king, int kingCol, int kingRow) {
        return checkKing(board.getPiece(kingCol-1, kingRow-1), king) ||
                checkKing(board.getPiece(kingCol+1, kingRow-1), king) ||
                checkKing(board.getPiece(kingCol, kingRow-1), king) ||
                checkKing(board.getPiece(kingCol-1, kingRow), king) ||
                checkKing(board.getPiece(kingCol+1, kingRow), king) ||
                checkKing(board.getPiece(kingCol-1, kingRow+1), king) ||
                checkKing(board.getPiece(kingCol+1, kingRow+1), king) ||
                checkKing(board.getPiece(kingCol, kingRow+1), king); 
    }
    
    private boolean checkKing(Piece p, Piece k) {
        return p != null && !board.sameTeam(p, k) && 
                p.name.equals("King");
    }
    
    private boolean hitByPawn(int col, int row, Piece king, int kingCol, int kingRow) {
        int colorVal = king.isWhite ? -1 : 1;
        return checkPawn(board.getPiece(kingCol + 1, kingRow + colorVal), king, col, row) ||
               checkPawn(board.getPiece(kingCol - 1, kingRow + colorVal), king, col, row); 
    }
    
    private boolean checkPawn(Piece p, Piece k, int col, int row) {
        return p != null && !board.sameTeam(p, k) && p.name.equals("Pawn");
    }
    
    public boolean isGameOver(Piece king) {
        if (king == null) return false;

        // Primero verificamos si el rey está en jaque
        boolean isInCheck = isKingChecked(new Move(board, king, king.col, king.row));
        
        // Verificamos si hay movimientos legales disponibles
        boolean hasLegalMoves = false;
        
        for (Piece piece : board.pieceList) {
            if (board.sameTeam(piece, king)) {
                // Guardar la pieza seleccionada actual
                Piece prevSelected = board.selectedPiece;
                board.selectedPiece = piece;

                for (int row = 0; row < board.rows; row++) {
                    for (int col = 0; col < board.cols; col++) {
                        Move move = new Move(board, piece, col, row);
                        if (board.isValidMove(move)) {
                            hasLegalMoves = true;
                            break;
                        }
                    }
                    if (hasLegalMoves) break;
                }
                board.selectedPiece = prevSelected;
                if (hasLegalMoves) break;
            }
        }
        
        // Si el rey está en jaque y no hay movimientos legales, es jaquemate
        // Si el rey no está en jaque y no hay movimientos legales, es tablas
        return !hasLegalMoves;
    }
    
}
