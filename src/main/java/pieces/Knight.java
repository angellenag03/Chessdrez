/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pieces;

import java.awt.image.BufferedImage;
import main.Board;

/**
 *
 * @author pausa
 */
public class Knight extends Piece{
    
    public Knight(Board board, int col, int row, boolean isWhite) {
        super(board);
        this.col = col;
        this.row = row;
        this.xPos = col * board.tileSize;
        this.yPos = row * board.tileSize;
        
        this.isWhite = isWhite;
        this.name = "Knight";
        
        this.sprite = sheet.getSubimage(2*sheetScale, isWhite ? 0 : sheetScale, sheetScale, sheetScale).
                getScaledInstance(board.tileSize, board.tileSize, BufferedImage.SCALE_SMOOTH);
    }
    
    @Override
    public boolean isValidMovement(int col, int row) {
        return Math.abs(col - this.col) * Math.abs(row - this.row) == 2;
    }

    public boolean moveCollidesWithPiece(int col, int row) {
        return false;
    }
    
}
