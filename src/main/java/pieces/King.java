package pieces;

import java.awt.image.BufferedImage;
import main.Board;
import main.Move;

/**
 *
 * @author angelsn
 */
public class King extends Piece{
    
    public King(Board board, int col, int row, boolean isWhite) {
        super(board);
        this.col = col;
        this.row = row;
        this.xPos = col * board.tileSize;
        this.yPos = row * board.tileSize;
        
        this.isWhite = isWhite;
        this.name = "King";
        
        this.sprite = sheet.getSubimage(5*sheetScale, isWhite ? 0 : sheetScale, sheetScale, sheetScale).
                getScaledInstance(board.tileSize, board.tileSize, BufferedImage.SCALE_SMOOTH);
    }
    
    @Override
    public boolean isValidMovement(int col, int row) { 
        return Math.abs((col - this.col) * (row - this.row)) == 1 || Math.abs(col - this.col) + Math.abs(row - this.row) == 1
                || canCastle(col, row);
    }
    
    private boolean canCastle(int col, int row) {
        if (this.row == row){
            if (col == 6){  // Enroque corto
                Piece rook = board.getPiece(7, row);
                if (rook != null && rook.isFirstMove && isFirstMove){
                    return board.getPiece(5, row) == null &&
                           board.getPiece(6, row) == null &&  // CORREGIDO
                           !board.checkScanner.isKingChecked(5, row, isWhite); // Check if the intermediate square is attacked
                }
            } else if (col == 2){  // Enroque largo
                Piece rook = board.getPiece(0, row);
                if (rook != null && rook.isFirstMove && isFirstMove){
                    return board.getPiece(3, row) == null &&
                           board.getPiece(2, row) == null &&
                           board.getPiece(1, row) == null &&
                           !board.checkScanner.isKingChecked(3, row, isWhite); // Check if the intermediate square is attacked
                }
            }
        }        
        return false;
    }
}
