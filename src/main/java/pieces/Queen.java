/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pieces;

import java.awt.image.BufferedImage;
import main.Board;

/**
 *
 * @author angelsn
 */
public class Queen extends Piece{
    
    public Queen(Board board, int col, int row, boolean isWhite) {
        super(board);
        this.col = col;
        this.row = row;
        this.xPos = col * board.tileSize;
        this.yPos = row * board.tileSize;
        
        this.isWhite = isWhite;
        this.name = "Queen";
        
        this.sprite = sheet.getSubimage(4*sheetScale, isWhite ? 0 : sheetScale, sheetScale, sheetScale).
                getScaledInstance(board.tileSize, board.tileSize, BufferedImage.SCALE_SMOOTH);
    }
    
    @Override
    public boolean isValidMovement(int col, int row) {
        // movimiento de torre
        return this.col == col || this.row == row || 
                // movimiento de alfil
                Math.abs(this.col - col) == (Math.abs(this.row - row));
    }
    
    @Override
    public boolean moveCollidesWithPiece(int col, int row) {
        // si se mueve como torre
        if (this.col == col || this.row == row) {
            // left
            if (this.col > col)
                for (int c = this.col - 1; c > col; c--) 
                    if (board.getPiece(c, this.row) != null)   
                        return true;
            // right
            if (this.col < col)  // CORREGIDO
                for (int c = this.col + 1; c < col; c++) 
                    if (board.getPiece(c, this.row) != null)   
                        return true;
            // up
            if (this.row > row)
                for (int r = this.row - 1; r > row; r--) 
                    if (board.getPiece(this.col, r) != null)   
                        return true;
            // down
            if (this.row < row)  // CORREGIDO
                for (int r = this.row + 1; r < row; r++) 
                    if (board.getPiece(this.col, r) != null)   
                        return true;
        } else {
            // movimiento diagonal (como alfil)
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
        }
        return false;
    }
}
