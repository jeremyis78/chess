/**
 * Copyright (C) 2010 Jeremy Brooks. ALL RIGHTS RESERVED.
 * Date: Jan 2, 2010
 */
package com.jeremybrooks.chess.util;

import java.io.PrintStream;
import java.util.List;

import com.jeremybrooks.chess.base.Bitmap;
import com.jeremybrooks.chess.base.Square;

/**
 * @author jeremy
 *
 */
public class Util {

    private static PrintStream err = System.err;

    /**
     * Give current time in milliseconds (as an int, not a long).
     * 
     * This method is applicable for use in successive calls that span less than
     * approximately 24 days (2^31 milliseconds), otherwise numerical overflow will occur.
     * 
     * @return an int representing {@code (int) (System.nanoTime() / 1000000)}
     */
    public static int milliTime()
    {
        return (int) (System.nanoTime() / 1000000);
    }
    
    /** 
     * convenience methods for determining non-zero given a
     * numerical argument
     */
    public static boolean bool(int i){ return i != 0; }
    public static boolean bool(long i){ return i != 0; }
    
    public static int opposing(int side){
        return side == Bitmap.WHITE ? Bitmap.BLACK : Bitmap.WHITE;
    }

    public static String displaySquaresStr(long b){
        StringBuffer sb = new StringBuffer();
        for (int i=0; i<64; i++){
            if(bool(b & (1L << i))){
                sb.append(Square.named(i) + ' ');
            }
        }
        return sb.toString();
    }
        
    public static int bitCount(long pieces)
    {
        return Long.bitCount(pieces);
    }
    
    /**
     * Return string of moves in Full Algebraic Notation (f2-f4, Qd1xd8, etc)
     * @param moves the list of moves to format
     * @return
     */
    public static String toFan(List<Integer> moves)
    {
        StringBuilder fan = new StringBuilder();
        for(int move: moves)
        {
            fan.append(displayMoveStr(move, false, false));
            fan.append(" ");
        }
        fan.deleteCharAt(fan.length() - 1);
        return fan.toString();
    }
    
    public static String displayMoveStr(int m, boolean check, boolean mate){
        int from = m & 0x3F;  //grab from square (6 bits)
        int to = (m >> 6) & 0x3F; //grab to square (6 bits)
        if(to == from && to == 0)
            return "<none>"; //noMove placeholder;
        int mov = (m >> 12) & 0x7; //grab moving piece (3bits)
        int cap = (m >> 15) & 0x7; //grab captured piece (3bits)
        int pro = (m >> 18) & 0x7; //grab promotion piece (3bits)


        char pieceChar[] = {' ','P','N','K',' ','B','R','Q'};
        StringBuilder coordStr = new StringBuilder(); 
        StringBuilder SANStr = new StringBuilder();

        //Add the moving piece
        coordStr.append(pieceChar[mov]);
        coordStr.append(Square.named(from));
        if (pieceChar[mov] == 'P' && bool(cap)){ //if pawn capture
            SANStr.append(coordStr.toString().charAt(1)); //the file of the pawn
        } else {
            SANStr.append(pieceChar[mov]);  //the type of piece
        }
        
        //Add 'x' or '-' for capture or noncapture    
        if (bool(cap)){
            //coordStr[i++] = SANStr[j++] = 'x';
            coordStr.append("x");
            SANStr.append("x");
        } else {
            coordStr.append('-');
        }

        //Add the 'to' square
        coordStr.append(Square.named(to));
        SANStr.append(Square.named(to));
        
        //Add the promotion piece
        if(bool(pro)){
            if (pieceChar[mov] == 'P'){
                coordStr.append(pieceChar[pro]);
                SANStr.append('=');
                SANStr.append(pieceChar[pro]);
            } else {
                err.println("can't promote a piece other than a pawn");
            }
        }

        if (mate || (check && mate)){
            coordStr.append("#");
            SANStr.append("#");
        } else if (check){
            coordStr.append("+");
            SANStr.append("+");
        }
        
        //Print the moves in coordinate notation and SAN (TODO: SAN doesn't account for ambiguous moves yet!)
//        if ('K' == pieceChar[mov]){
//            if ((from == Bitmap.E1 && to == Bitmap.G1) || (from == Bitmap.E8 && to == Bitmap.G8)){
//                coordStr.append(" 0-0");
//            } else if ((from == Bitmap.E1 && to == Bitmap.C1) || (from == Bitmap.E8 && to == Bitmap.C8)){
//                coordStr.append(" 0-0-0");
//            } 
//        }
        return coordStr.toString();
    }
   
    /**
     * Pretty print the squares represented within the bitmap.  
     * If no squares (bits) are found, then "NONE" is returned.
     * 
     * @param bitmap the bitmap containing bits representing named squares 
     * @return a string of the named squares (bits) set in bitmap
     */
    public static String formatSquares(long bitmap)
    {
        StringBuilder formatted = new StringBuilder();
        int squareOfPiece = 0;
        boolean isFirst = true;
        while(bitmap != 0)
        {
            if(!isFirst) formatted.append(" ");
            isFirst = false;
            squareOfPiece = Bitmap.lowestBitNumber(bitmap);
            formatted.append(Square.named(squareOfPiece));
            bitmap = Bitmap.clearBit(bitmap, squareOfPiece);
        }
        String formattedSquares = formatted.toString().trim();
        return formattedSquares.isEmpty() ? "NONE" : formattedSquares;
    }

    

    /**
     * Formats the given bitmap printing "X"'s for one bits (bits set)
     * and "-"'s for zero bits.  The order of printing is from least
     * significant bit to most significant bit
     * 
     * Example:
     * Calling formatBitmap(5) yields  "X - X - - - - -"
     * Calling formatBitmap(11) yields "X X - X - - - -"
     * @param bitmap the bitmap to format
     * @return
     */
    public static String formatByteBitmap(byte bitmap) {
        byte mask = 1;
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < 8; i++, mask <<= 1){
            //If there's a bit at that square print "X" otherwise "-"
            if (bool(mask & bitmap)) sb.append("X ");
            else sb.append("- ");
        }    
        //Trim last space and return
        return sb.toString().trim();
    }

    public static String formatByteBitmap(String header, byte bitmap){
        return header + formatByteBitmap(bitmap);
    }
    
    
    /**
     * Convenience method: formats only least significant byte
     * 
     */
    public static String formatByteBitmap(short bitmap) {
        if (bool(bitmap & 0xff00)){
            throw new IllegalArgumentException(
                    "bitmap should have only bits in its least significant byte set");
        }
        return formatByteBitmap((byte) bitmap);
    }

    /**
     * Convenience method: formats only least significant byte
     * 
     */
    public static String formatByteBitmap(int bitmap) {
        if (bool(bitmap & 0xffffff00)){
            throw new IllegalArgumentException(
                    "bitmap should have only bits in its least significant byte set");
        }
        return formatByteBitmap((byte) bitmap);
    }

    /**
     * Formats three bitmaps as described below.  This function is mainly
     * for debugging and testing purposes.
     * 
     * piece   : - - - - X - - -
     * occupied: - X - - - - - -
     * attacks   : - X X X - X X X
     * 
     * 
     * "piece" row:      X is the bit set in the least significant byte of pieceBitmap
     * 
     * "occupied" row:     the X's are the other pieces (bits set) in the least sig. 
     *                     byte of occupied bitmap
     * 
     * "attacks" row:     the X's indicate the possible moves (bits set) in movesBitmap   
     * 
     * @param pieceBitmap the bitmap where the piece sits on the rank 
     * @param occupiedBitmap the bitmap indicating which pieces are present on the rank
     * @param attackBitmap the bitmap indicating the possible moves
     */
    public static String formatBaseAttacks(int pieceBitmap, int occupiedBitmap, int attackBitmap){
        if (bool(0xFFFFFF00 & pieceBitmap)){
            throw new IllegalArgumentException("pieceBitmap: " + Integer.toBinaryString(pieceBitmap) + " can only have bits set in the least significant byte");
        }
        if (bool(0xFFFFFF00 & occupiedBitmap)){
            throw new IllegalArgumentException("occupiedBitmap: " + Integer.toBinaryString(occupiedBitmap) + " can only have bits set in the least significant byte");
        }
        if (bool(0xFFFFFF00 & attackBitmap)){
            throw new IllegalArgumentException("attackBitmap: " + Integer.toBinaryString(attackBitmap) + " can only have bits set in the least significant byte");
        }
        if (bitCount(pieceBitmap) > 1){
            throw new IllegalArgumentException("pieceBitmap: " + Integer.toBinaryString(pieceBitmap) + " can have only a single bit set");
        }
        return     formatByteBitmap("piece   : ", (byte)pieceBitmap) + "\n" +
                formatByteBitmap("occupied: ", (byte)occupiedBitmap) + "\n" +
                formatByteBitmap("attacks : ", (byte)attackBitmap) + "\n";
    }

    //******************************************************************/
    //              The bits of an EncodedMove                         */
    //******************************************************************/
    // +--------+--------+---------+----------+-----------+---------+  */
    // | 0 - 5  | 6 - 11 | 12 - 14 | 15 - 17  |  18 - 20  | 21 - 31 |  */
    // +--------+--------+---------+----------+-----------+---------+  */
    // |  from  |   to   | moving  | captured | promotion | unused  |  */
    // | square | square | piece   | piece    |   piece   |         |  */
    // +--------+--------+---------+----------+-----------+---------+  */
    //                                                                 */
    //******************************************************************/
    public static int EncodeMove (int from, int to, int mov, int cap, int pro)
    {
        return (from | (to << 6) | (mov << 12) | (cap << 15) | (pro << 18));
    }
    
}
