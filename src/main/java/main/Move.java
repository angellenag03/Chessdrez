/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

import pieces.Piece;

/**
 *
 * @author angelsn
 */
public class Move {
    int oldCol, oldRow;
    int newCol, newRow;
    
    Piece piece;
    Piece capture;
    
    public Move(Board board, Piece piece, int newCol, int newRow){
        this.oldCol = piece.col;
        this.oldRow = piece.row;
        this.newCol = newCol;
        this.newRow = newRow;
        
        this.piece = piece;
        this.capture = board.getPiece(newCol, newRow);
    }
}
