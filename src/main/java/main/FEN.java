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

/**
 * Implementación de Forsyth-Edwards Notation (FEN) para ajedrez.
 * FEN es un formato estándar para describir posiciones de ajedrez usando una sola línea de texto.
 * 
 * Un FEN consiste en 6 campos separados por espacios:
 * 1. Piece Placement: Ubicación de las piezas (ej: "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR")
 * 2. Active Color: Turno actual ('w' o 'b')
 * 3. Castling Rights: Derechos de enroque ('KQkq', '-', etc.)
 * 4. En Passant Target: Casilla objetivo de en passant ('-' o coordenada algebraica)
 * 5. Halfmove Clock: Movimientos desde última captura o avance de peón
 * 6. Fullmove Number: Número de movimientos completos
 * 
 * @see <a href="https://www.chess.com/terms/fen-chess">Chess.com FEN Documentation</a>
 */
public class FEN {
    private Board board;
    
    public FEN(Board board) {
        this.board = board;
    }
    
    /**
     * Genera la notación FEN del estado actual del tablero.
     * @return String en formato FEN
     */
    public String generateFEN() {
        StringBuilder fen = new StringBuilder();
        
        // 1. Piece placement
        fen.append(generatePiecePlacement()).append(" ");
        
        // 2. Active color
        fen.append(board.isWhiteToMove ? "w" : "b").append(" ");
        
        // 3. Castling rights
        fen.append(generateCastlingAvailability()).append(" ");
        
        // 4. En passant target
        fen.append(generateEnPassantTarget()).append(" ");
        
        // 5. Halfmove clock
        fen.append(board.halfmoveClock).append(" ");
        
        // 6. Fullmove number
        fen.append(board.fullmoveNumber);
        
        return fen.toString();
    }
    
    /**
     * Genera la parte de colocación de piezas del FEN.
     * Comienza desde la octava fila hasta la primera.
     * @return String representando la colocación de piezas
     */
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
    
    /**
     * Genera la parte de derechos de enroque del FEN.
     * @return String representando los derechos de enroque disponibles
     */
    private String generateCastlingAvailability() {
        StringBuilder castling = new StringBuilder();
        if (board.whiteCanCastleKingside) castling.append('K');
        if (board.whiteCanCastleQueenside) castling.append('Q');
        if (board.blackCanCastleKingside) castling.append('k');
        if (board.blackCanCastleQueenside) castling.append('q');
        if (castling.length() == 0) castling.append('-');
        return castling.toString();
    }
    
    /**
     * Genera la parte de objetivo de en passant del FEN.
     * @return String representando la casilla objetivo de en passant
     */
    private String generateEnPassantTarget() {
        if (board.enPassantTile == -1) {
            return "-";
        } else {
            int col = board.enPassantTile % 8;
            int row = board.enPassantTile / 8;
            return String.format("%c%d", 'a' + col, 8 - row);
        }
    }
    
    /**
     * Obtiene el carácter FEN para una pieza.
     * @param piece La pieza a convertir
     * @return Carácter FEN correspondiente a la pieza
     */
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
     * Carga el estado del tablero desde una notación FEN.
     * @param fen String en formato FEN
     * @throws IllegalArgumentException si el FEN es inválido
     */
    public void loadFromFEN(String fen) {
        String[] parts = fen.split(" ");
        if (parts.length != 6) {
            throw new IllegalArgumentException("Invalid FEN: must have 6 parts");
        }
        
        // Validar cada parte del FEN
        validatePiecePlacement(parts[0]);
        validateActiveColor(parts[1]);
        validateCastlingAvailability(parts[2]);
        validateEnPassantTarget(parts[3]);
        validateHalfmoveClock(parts[4]);
        validateFullmoveNumber(parts[5]);
        
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
    
    /**
     * Valida la parte de colocación de piezas del FEN.
     * @param placement String de colocación de piezas
     * @throws IllegalArgumentException si el formato es inválido
     */
    private void validatePiecePlacement(String placement) {
        String[] ranks = placement.split("/");
        if (ranks.length != 8) {
            throw new IllegalArgumentException("Invalid piece placement: must have 8 ranks");
        }
        
        for (String rank : ranks) {
            int squares = 0;
            for (char c : rank.toCharArray()) {
                if (Character.isDigit(c)) {
                    squares += Character.getNumericValue(c);
                } else if ("pnbrqkPNBRQK".indexOf(c) != -1) {
                    squares++;
                } else {
                    throw new IllegalArgumentException("Invalid piece placement: invalid character " + c);
                }
            }
            if (squares != 8) {
                throw new IllegalArgumentException("Invalid piece placement: rank must have 8 squares");
            }
        }
    }
    
    /**
     * Valida la parte de color activo del FEN.
     * @param color String de color activo
     * @throws IllegalArgumentException si el formato es inválido
     */
    private void validateActiveColor(String color) {
        if (!color.equals("w") && !color.equals("b")) {
            throw new IllegalArgumentException("Invalid active color: must be 'w' or 'b'");
        }
    }
    
    /**
     * Valida la parte de derechos de enroque del FEN.
     * @param castling String de derechos de enroque
     * @throws IllegalArgumentException si el formato es inválido
     */
    private void validateCastlingAvailability(String castling) {
        if (!castling.equals("-") && !castling.matches("[KQkq]+")) {
            throw new IllegalArgumentException("Invalid castling availability: must be '-' or combination of 'KQkq'");
        }
    }
    
    /**
     * Valida la parte de objetivo de en passant del FEN.
     * @param enPassant String de objetivo de en passant
     * @throws IllegalArgumentException si el formato es inválido
     */
    private void validateEnPassantTarget(String enPassant) {
        if (!enPassant.equals("-") && !enPassant.matches("[a-h][3-6]")) {
            throw new IllegalArgumentException("Invalid en passant target: must be '-' or valid square (e.g., 'e3')");
        }
    }
    
    /**
     * Valida la parte de contador de medio movimiento del FEN.
     * @param halfmove String de contador de medio movimiento
     * @throws IllegalArgumentException si el formato es inválido
     */
    private void validateHalfmoveClock(String halfmove) {
        try {
            int value = Integer.parseInt(halfmove);
            if (value < 0) {
                throw new IllegalArgumentException("Invalid halfmove clock: must be non-negative");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid halfmove clock: must be a number");
        }
    }
    
    /**
     * Valida la parte de número de movimiento completo del FEN.
     * @param fullmove String de número de movimiento completo
     * @throws IllegalArgumentException si el formato es inválido
     */
    private void validateFullmoveNumber(String fullmove) {
        try {
            int value = Integer.parseInt(fullmove);
            if (value < 1) {
                throw new IllegalArgumentException("Invalid fullmove number: must be positive");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid fullmove number: must be a number");
        }
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

    /**
    * Convierte la diferencia entre dos FENs a notación algebraica.
    * @param prevFEN FEN anterior
    * @param currentFEN FEN actual
    * @return Movimiento en notación estándar (ej: "e4", "Nxf3", "O-O", "exd6 e.p.")
    */
   public String convertFENtoMove(String prevFEN, String currentFEN) {
       // Validación básica
       String[] prevParts = prevFEN.split(" ");
       String[] currentParts = currentFEN.split(" ");
       if (prevParts.length != 6 || currentParts.length != 6) {
           return "???"; // FEN inválido
       }

       boolean isWhiteTurn = prevParts[1].equals("w");

       // --- 1. Detectar enroques (prioridad máxima) ---
       String prevCastling = prevParts[2];
       String currentCastling = currentParts[2];

       // Enroque blanco
       if (isWhiteTurn) {
           if (prevCastling.contains("K") && !currentCastling.contains("K")) return "O-O";
           if (prevCastling.contains("Q") && !currentCastling.contains("Q")) return "O-O-O";
       } 
       // Enroque negro
       else {
           if (prevCastling.contains("k") && !currentCastling.contains("k")) return "O-O";
           if (prevCastling.contains("q") && !currentCastling.contains("q")) return "O-O-O";
       }

       // --- 2. Procesar movimientos normales ---
       String[] prevRanks = prevParts[0].split("/");
       String[] currentRanks = currentParts[0].split("/");

       // Encontrar la pieza movida y su destino
       int fromCol = -1, fromRow = -1;
       int toCol = -1, toRow = -1;
       char movedPiece = ' ';
       boolean isCapture = false;
       boolean isEnPassant = false;
       boolean isPromotion = false;

       // Comparar cada casilla del tablero
       for (int row = 0; row < 8; row++) {
           for (int col = 0; col < 8; col++) {
               char prevPiece = getPieceAt(prevRanks, col, row);
               char currentPiece = getPieceAt(currentRanks, col, row);

               if (prevPiece != currentPiece) {
                   if (prevPiece != '1' && currentPiece == '1') {
                       // Casilla de origen (se vació)
                       fromCol = col;
                       fromRow = row;
                       movedPiece = prevPiece;
                   } else if (prevPiece == '1' && currentPiece != '1') {
                       // Casilla de destino (se llenó)
                       toCol = col;
                       toRow = row;
                   } else if (prevPiece != '1' && currentPiece != '1') {
                       // Captura (pieza diferente en la misma casilla)
                       toCol = col;
                       toRow = row;
                       isCapture = true;
                       movedPiece = currentPiece;
                   }
               }
           }
       }

       // --- 3. Validar y construir la notación ---
       if (fromCol == -1 || toCol == -1) return "???"; // Movimiento no detectado

       // Detectar captura al paso (peón que captura en diagonal pero no hay pieza en destino)
       if (Character.toLowerCase(movedPiece) == 'p' && 
           Math.abs(fromCol - toCol) == 1 && !isCapture) {
           isEnPassant = true;
           isCapture = true; // Se marca como captura
       }

       // Detectar promoción (peón llega a la última fila)
       if (Character.toLowerCase(movedPiece) == 'p' && 
           (toRow == 0 || toRow == 7)) {
           isPromotion = true;
       }

       // Construir notación algebraica
       StringBuilder move = new StringBuilder();

       // Pieza (excepto peones)
       if (Character.toLowerCase(movedPiece) != 'p') {
           move.append(Character.toUpperCase(movedPiece));
       }

       // Casilla de origen (para ambigüedades, ej: "Nbd2")
       // (Omitido por simplicidad, pero se puede añadir)

       // Captura
       if (isCapture) {
           if (Character.toLowerCase(movedPiece) == 'p') {
               move.append((char)('a' + fromCol)); // Columna del peón (ej: "exd4")
           }
           move.append('x');
       }

       // Casilla de destino
       move.append((char)('a' + toCol));
       move.append(8 - toRow);

       // Captura al paso
       if (isEnPassant) {
           move.append(" e.p.");
       }

       // Promoción (ej: "e8=Q")
       if (isPromotion) {
           move.append('=').append(Character.toUpperCase(currentRanks[toRow].charAt(toCol)));
       }

       return move.toString();
   }

    /**
     * Obtiene el símbolo de la pieza en una columna y fila dada de la disposición del tablero.
     * @param ranks Arreglo de strings representando las filas del tablero en notación FEN
     * @param col Columna (0-7)
     * @param row Fila (0-7)
     * @return Símbolo de la pieza ('p', 'n', 'b', 'r', 'q', 'k' o '1' si está vacío)
     */
    private char getPieceAt(String[] ranks, int col, int row) {
        if (row < 0 || row >= 8 || col < 0 || col >= 8) return ' ';
        String rank = ranks[row];
        int currentColumn = 0;
        for (char c : rank.toCharArray()) {
            if (Character.isDigit(c)) {
                currentColumn += Character.getNumericValue(c);
            } else {
                if (currentColumn == col) {
                    return c;
                }
                currentColumn++;
            }
        }
        return '1'; // Casilla vacía
    }
}
