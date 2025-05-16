/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pieces;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import main.Board;

/**
 *
 * @author pausa
 */
public abstract class Piece {
    
    public int col, row;
    public int xPos, yPos;
    
    public boolean isWhite;
    public String name;
    public int value;
    
    public boolean isFirstMove = true;
    
    BufferedImage sheet;
    {
        try {
            sheet = ImageIO.read(ClassLoader.getSystemResourceAsStream("pieces.png"));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Ha ocurrido un error"+e.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    protected int sheetScale = sheet.getWidth()/6;
    
    Image sprite;
    Board board;

    public Piece(Board board){ this.board = board; }
    
    public boolean isValidMovement(int col, int row) { return true; }
    public boolean moveCollidesWithPiece(int col, int row) { return false; }
    
    public void paint(Graphics2D g2d) {
        g2d.drawImage(sprite, xPos, yPos, null);
    }
}
