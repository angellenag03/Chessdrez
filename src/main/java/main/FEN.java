/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

import pieces.Bishop;
import pieces.King;
import pieces.Knight;
import pieces.Pawn;
import pieces.Piece;
import pieces.Queen;
import pieces.Rook;

public class FEN {
    private Board board;
    
    // FEN components
    private String piecePlacement;
    private String activeColor;
    private String castlingAvailability;
    private String enPassantTarget;
    private String halfmoveClock;
    private String fullmoveNumber;
    
    public FEN(Board board) {
        this.board = board;
    }
    
    /**
     * Generates FEN notation from current board state
     */
    public String generateFEN() {
        StringBuilder fen = new StringBuilder();
        
        // 1. Piece placement
        fen.append(generatePiecePlacement()).append(" ");
        
        // 2. Active color
        fen.append(board.isWhiteToMove ? "w" : "b").append(" ");
        
        // 3. Castling availability
        fen.append(generateCastlingAvailability()).append(" ");
        
        // 4. En passant target
        fen.append(generateEnPassantTarget()).append(" ");
        
        // 5. Halfmove clock
        fen.append(board.halfmoveClock).append(" ");
        
        // 6. Fullmove number
        fen.append(board.fullmoveNumber);
        
        return fen.toString();
    }
    
    private String generatePiecePlacement() {
        StringBuilder placement = new StringBuilder();
        for (int row = 0; row < 8; row++) {
            int emptySquares = 0;
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPiece(col, row);
                if (piece == null) {
                    emptySquares++;
                } else {
                    if (emptySquares > 0) {
                        placement.append(emptySquares);
                        emptySquares = 0;
                    }
                    placement.append(getFENCharForPiece(piece));
                }
            }
            if (emptySquares > 0) {
                placement.append(emptySquares);
            }
            if (row < 7) {
                placement.append('/');
            }
        }
        return placement.toString();
    }
    
    private String generateCastlingAvailability() {
        StringBuilder castling = new StringBuilder();
        if (board.whiteCanCastleKingside) castling.append('K');
        if (board.whiteCanCastleQueenside) castling.append('Q');
        if (board.blackCanCastleKingside) castling.append('k');
        if (board.blackCanCastleQueenside) castling.append('q');
        if (castling.length() == 0) castling.append('-');
        return castling.toString();
    }
    
    private String generateEnPassantTarget() {
        if (board.enPassantTile == -1) {
            return "-";
        } else {
            int col = board.enPassantTile % 8;
            int row = board.enPassantTile / 8;
            return String.format("%c%d", 'a' + col, 8 - row);
        }
    }
    
    private char getFENCharForPiece(Piece piece) {
        char fenChar;
        switch (piece.name) {
            case "Pawn": fenChar = 'p'; break;
            case "Rook": fenChar = 'r'; break;
            case "Knight": fenChar = 'n'; break;
            case "Bishop": fenChar = 'b'; break;
            case "Queen": fenChar = 'q'; break;
            case "King": fenChar = 'k'; break;
            default: throw new IllegalArgumentException("Unknown piece type: " + piece.name);
        }
        return piece.isWhite ? Character.toUpperCase(fenChar) : fenChar;
    }
    
    /**
     * Loads board state from FEN notation
     */
    public void loadFromFEN(String fen) {
        String[] parts = fen.split(" ");
        if (parts.length != 6) {
            throw new IllegalArgumentException("Invalid FEN: must have 6 parts");
        }
        
        // Clear current board
        board.pieceList.clear();
        
        // 1. Piece placement
        loadPiecePlacement(parts[0]);
        
        // 2. Active color
        board.isWhiteToMove = parts[1].equals("w");
        
        // 3. Castling availability
        loadCastlingAvailability(parts[2]);
        
        // 4. En passant target
        loadEnPassantTarget(parts[3]);
        
        // 5. Halfmove clock
        board.halfmoveClock = Integer.parseInt(parts[4]);
        
        // 6. Fullmove number
        board.fullmoveNumber = Integer.parseInt(parts[5]);
        
        // Update first move flags based on castling rights
        updateFirstMoveFlags();
    }
    
    private void loadPiecePlacement(String placement) {
        String[] ranks = placement.split("/");
        for (int row = 0; row < 8; row++) {
            int col = 0;
            for (char c : ranks[row].toCharArray()) {
                if (Character.isDigit(c)) {
                    col += Character.getNumericValue(c);
                } else {
                    board.pieceList.add(createPieceFromFENChar(c, col, row));
                    col++;
                }
            }
        }
    }
    
    private void loadCastlingAvailability(String castling) {
        board.whiteCanCastleKingside = castling.contains("K");
        board.whiteCanCastleQueenside = castling.contains("Q");
        board.blackCanCastleKingside = castling.contains("k");
        board.blackCanCastleQueenside = castling.contains("q");
    }
    
    private void loadEnPassantTarget(String enPassant) {
        if (enPassant.equals("-")) {
            board.enPassantTile = -1;
        } else {
            int col = enPassant.charAt(0) - 'a';
            int row = 8 - Character.getNumericValue(enPassant.charAt(1));
            board.enPassantTile = row * 8 + col;
        }
    }
    
    private Piece createPieceFromFENChar(char fenChar, int col, int row) {
        boolean isWhite = Character.isUpperCase(fenChar);
        char lowerChar = Character.toLowerCase(fenChar);
        
        switch (lowerChar) {
            case 'p': return new Pawn(board, col, row, isWhite);
            case 'r': return new Rook(board, col, row, isWhite);
            case 'n': return new Knight(board, col, row, isWhite);
            case 'b': return new Bishop(board, col, row, isWhite);
            case 'q': return new Queen(board, col, row, isWhite);
            case 'k': return new King(board, col, row, isWhite);
            default: throw new IllegalArgumentException("Invalid FEN character: " + fenChar);
        }
    }
    
    private void updateFirstMoveFlags() {
        // If castling rights are missing, king has moved
        Piece whiteKing = board.findKing(true);
        Piece blackKing = board.findKing(false);
        
        if (whiteKing != null) {
            whiteKing.isFirstMove = board.whiteCanCastleKingside || board.whiteCanCastleQueenside;
        }
        
        if (blackKing != null) {
            blackKing.isFirstMove = board.blackCanCastleKingside || board.blackCanCastleQueenside;
        }
        
        // Rooks for castling
        Piece whiteKingsideRook = board.getPiece(7, 7);
        Piece whiteQueensideRook = board.getPiece(0, 7);
        Piece blackKingsideRook = board.getPiece(7, 0);
        Piece blackQueensideRook = board.getPiece(0, 0);
        
        if (whiteKingsideRook != null && whiteKingsideRook.name.equals("Rook")) {
            whiteKingsideRook.isFirstMove = board.whiteCanCastleKingside;
        }
        if (whiteQueensideRook != null && whiteQueensideRook.name.equals("Rook")) {
            whiteQueensideRook.isFirstMove = board.whiteCanCastleQueenside;
        }
        if (blackKingsideRook != null && blackKingsideRook.name.equals("Rook")) {
            blackKingsideRook.isFirstMove = board.blackCanCastleKingside;
        }
        if (blackQueensideRook != null && blackQueensideRook.name.equals("Rook")) {
            blackQueensideRook.isFirstMove = board.blackCanCastleQueenside;
        }
    }
}
