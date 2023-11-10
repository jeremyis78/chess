package com.jeremybrooks.chess.base;

import static com.jeremybrooks.chess.base.Bitmap.*;


/**
 * <pre>
 * Represents a square on the chess board.
 * 
 * A Square has one of two states: 
 *      1) empty/unoccupied, 
 *              {@code square.isOccupied() == false}
 *      2) occupied by a piece,
 *              {@code square.isOccupied() == true}
 * </pre>
 * @author jeremy
 *
 */
public class Square {
    
    private Piece piece;
    
    /**
     * Constructs an unoccupied square
     */
    public Square() {
        super();
        set(null);
    }
    
    /**
     * Constructs a square containing the given piece
     * 
     * @param piece the piece on this square
     */
    public Square(Piece piece) {
        super();
        set(piece);
    }

    public boolean isOccupied()
    {
        if (piece != null && piece.exists()) 
            return true;
        return false;
    }

    public void clear()
    {
        set(null);
    }

    public Piece get()
    {
        if (piece == null) return new Empty();
        return piece;
    }

    public void set(Piece piece)
    {
        this.piece = piece;
    }

    /**
     * Returns the corresponding square from the square name given in the argument
     * This is the inverse of {@link #named(int)}.
     * 
     * @param str the name of a square (e.g. e4) in human readable format
     * @return an int corresponding to the named square
     */
    public static int squareOf(final String str) {
        int x, y;
        String s = str.toLowerCase();
        if (s.length() == 2){
            if (s.charAt(0) >= 'a' && s.charAt(0) <= 'h' && s.charAt(1) >= '1' && s.charAt(1) <= '8'){
                x = s.charAt(0) - 'a'; 
                y = s.charAt(1) - '1';
                
                //Compute linear index
                return (y * 8) + x;        
            } else
                return NOSQUARE;
        } else
            return NOSQUARE;
    }

    /**
     * Return the name of the given square.
     * This is the inverse of {@link #squareOf(String)}.
     * 
     * @param sq the int representing the square
     * @return the named square in human readable format
     */
    public static String named(int sq){
        String s = ""; 
        if (sq >= Bitmap.A1 && sq <= Bitmap.H8){
            s += (char)('a' + (sq % 8));
            s += (char)('1' + (sq / 8)); //int division
        } 
        return s;
    }

    public static boolean adjacentSquares(int sq1, int sq2){
        int x1 = Bitmap.fileNumber(sq1);//(sq1 % 8);
        int y1 = Bitmap.rankNumber(sq1);//(sq1 / 8);  //integer division
        int x2 = Bitmap.fileNumber(sq2);//(sq2 % 8);
        int y2 = Bitmap.rankNumber(sq2);//(sq2 / 8);  //integer division
        
        //Squares sq1 and sq2 are adjacent if the square of 
        //the distance between them is less than or equal to two.
        
        int d2 = (x2 - x1)*(x2 - x1) + (y2 - y1)*(y2 - y1); 
        
        if (d2 <= 2)
            return true;
        return false;
    }

    // minusOneRank
    //
    // Returns the from square given the square
    // the pawn moved to. (pawn advanced one square)
    //
    public static int squareBehind(int currentSquare, int side){
        return (side == Piece.WHITE) ? (currentSquare - 8) : (currentSquare + 8);
    }

    // minusTwoRank
    //
    // Returns the from square given the square
    // the pawn moved to. (pawn advanced two squares)
    //
    public static int twoSquaresBehind(int currentSquare, int side){
        return Square.squareBehind(Square.squareBehind(currentSquare, side), side);
    }

    public static int squareAhead(int currentSquare, int side){
        return (side == Piece.WHITE) ? (currentSquare + 8) : (currentSquare - 8);
    }

    public static int squareLeftOf(int currentSquare){
        return squareLeftOf(currentSquare, 0);
    }

    @Deprecated
    public static int squareLeftOf(int currentSquare, int side){
        return (currentSquare - 1);
    }

    public static int squareRightOf(int currentSquare){
        return squareRightOf(currentSquare, 0);
    }

    @Deprecated
    public static int squareRightOf(int currentSquare, int side){
        return (currentSquare + 1);
    }

    public static boolean isOnLeftEdgeOfBoard(int square) {
        return fileNumber(square) == A1;
    }

    public static boolean isOnRightEdgeOfBoard(int square) {
        return fileNumber(square + 1) == A1;
    }

    public static boolean isOnGFile(int currentSquare){
        return (fileNumber(currentSquare) == G1);
    }

    public static boolean isOnCFile(int currentSquare){
        return (fileNumber(currentSquare) == C1);
    }

    public static boolean notOnSixthRank(int currentSquare) {
        return (rankNumber(currentSquare) != 5); //zero-based rank
    }

    public static boolean notOnThirdRank(int currentSquare) {
        return (rankNumber(currentSquare) != 2); //zero-based rank
    }

    public static boolean isEighthRank(int currentSquare, int side) {
        return side == Piece.WHITE ? 7 == rankNumber(currentSquare) : 0 == rankNumber(currentSquare); //zero-based rank 
    }
}
