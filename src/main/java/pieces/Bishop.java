package pieces;

import java.awt.image.BufferedImage;
import main.Board;

/**
 *
 * @author Ricardo & angelsn
 */
public class Bishop extends Piece{
    
    public Bishop(Board board, int col, int row, boolean isWhite) {
        super(board);
        this.col = col;
        this.row = row;
        this.xPos = col * board.tileSize;
        this.yPos = row * board.tileSize;
        
        this.isWhite = isWhite;
        this.name = "Bishop";
        
        this.sprite = sheet.getSubimage(3*sheetScale, isWhite ? 0 : sheetScale, sheetScale, sheetScale).
                getScaledInstance(board.tileSize, board.tileSize, BufferedImage.SCALE_SMOOTH);
    }
    
    @Override
    public boolean isValidMovement(int col, int row) {
        return Math.abs(this.col - col) == (Math.abs(this.row - row));
    }
    
    public boolean moveCollidesWithPiece(int col, int row) {
        int colDirection = Integer.compare(col, this.col);
        int rowDirection = Integer.compare(row, this.row);

        int currentCol = this.col + colDirection;
        int currentRow = this.row + rowDirection;

        while (currentCol != col && currentRow != row) {
            if (board.getPiece(currentCol, currentRow) != null) {
                return true;
            }
            currentCol += colDirection;
            currentRow += rowDirection;
        }

        return false;
    }

}
