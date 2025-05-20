package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import pieces.*;
import sfx.SFXManager;

public class Board extends JPanel {
    // Board dimensions and properties
    public int tileSize = 65;
    public int cols = 8;
    public int rows = 8;
    
    // Game state tracking
    public ArrayList<Piece> pieceList = new ArrayList<>();
    public Piece selectedPiece;
    public boolean isWhiteToMove = true;
    public boolean isGameOver = false;
    public boolean wasInCheckLastTurn = false;
    
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
    }

    public MoveHistoryPanel getMoveHistoryPanel() {
       return moveHistoryPanel;
    }   
    
    public void makeMove(Move move) {
        Piece currentKing = findKing(isWhiteToMove);
        boolean wasInCheckBeforeMove = checkScanner.isKingChecked(
            new Move(this, currentKing, currentKing.col, currentKing.row));
        
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
        
        if (wasInCheckBeforeMove && !wasInCheckLastTurn) {
            System.out.println((!isWhiteToMove ? "White" : "Black") + " is no longer in check!");
        }
        
        gameHistory.add(generateFEN());
        moveHistoryPanel.updateMoveHistory(gameHistory);
        printCurrentTurn();
        checkForCheck();
        updateGameState();
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

    public boolean isValidMove(Move move) {
        if (isGameOver) return false;
        if (move.piece.isWhite != isWhiteToMove) return false;
        if (sameTeam(move.piece, move.capture)) return false;
        if (!move.piece.isValidMovement(move.newCol, move.newRow)) return false;
        if (move.piece.moveCollidesWithPiece(move.newCol, move.newRow)) return false;
        if (checkScanner.isKingChecked(move)) return false;
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

    private void checkForCheck() {
        Piece king = findKing(isWhiteToMove);
        boolean isCurrentlyInCheck = checkScanner.isKingChecked(
            new Move(this, king, king.col, king.row));
        
        if (isCurrentlyInCheck && !wasInCheckLastTurn) {
            System.out.println("CHECK! " + (isWhiteToMove ? "White" : "Black") + " king is in check!");
            wasInCheckLastTurn = true;
        } else if (!isCurrentlyInCheck && wasInCheckLastTurn) {
            wasInCheckLastTurn = false;
        } else if (isCurrentlyInCheck) {
            System.out.println("Still in CHECK! " + (isWhiteToMove ? "White" : "Black") + " king must move!");
        }
    }

    private void updateGameState() {
        if (isGameOver) return;
        
        Piece king = findKing(isWhiteToMove);
        if (checkScanner.isGameOver(king)) {
            boolean isInCheck = checkScanner.isKingChecked(new Move(this, king, king.col, king.row));
            
            System.out.println("\n" + "=".repeat(50));
            if (isInCheck) {
                System.out.println("CHECKMATE! " + (!isWhiteToMove ? "White" : "Black") + " Wins!");
            } else {
                System.out.println("STALEMATE! Draw game.");
            }
            System.out.println("Final FEN: " + generateFEN());
            System.out.println("=".repeat(50));
            isGameOver = true;
            printGameHistory();
        }
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