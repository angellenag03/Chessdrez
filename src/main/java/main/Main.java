/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import javax.swing.JFrame;

/**
 *
 * @author pausa
 */
public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.getContentPane().setBackground(Color.BLACK);
        frame.setLayout(new GridBagLayout());
        frame.setMinimumSize(new Dimension(1000,1000));
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        Board b = new Board();
        frame.add(b);
        
        frame.setVisible(true);
    }
}
