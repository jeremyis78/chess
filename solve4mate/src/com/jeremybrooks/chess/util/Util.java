/**
 * Copyright (C) 2010 Jeremy Brooks. ALL RIGHTS RESERVED.
 * Date: Jan 2, 2010
 */
package com.jeremybrooks.chess.util;

import static com.jeremybrooks.chess.base.Square.named;
import static com.jeremybrooks.chess.util.AbstractDisplayer.EOL;
import static org.junit.Assert.assertEquals;

import java.util.List;

import com.jeremybrooks.chess.base.Bitmap;
import com.jeremybrooks.chess.base.Empty;
import com.jeremybrooks.chess.base.GameState;
import com.jeremybrooks.chess.base.Piece;
import com.jeremybrooks.chess.base.PieceFactory;
import com.jeremybrooks.chess.base.Square;

/**
 * @author jeremy
 *
 */
public class Util {

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
        return side == Piece.WHITE ? Piece.BLACK : Piece.WHITE; //it could be: side ^ 1
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

    public static String displayMoveStr(int move, boolean check, boolean mate){
    	return formatMoveInFan(move, null);
    }
    
    /**
     * Format move in Full Algebraic Notation (FAN).
     * 
     * Formatted moves look like this:
     * 		Pe2e4
     * 		Ke1e2
     *      Ke1g1   (castling)
     * 		Bd4xe5  (capture)
     * 		Pc7c8Q  (promotion)
     *      Pb2a1R  (capture and promotion)
     *      
     * If a non-null GameState is given, valid en passant captures will
     * format as Pe5xf6 instead of Pe5-f6.
     *      
     * @param move the encoded move
     * @param state the current state of the game
     * @return
     */
    public static String formatMoveInFan(int move, GameState state){
        int from = move & 0x3F;  //grab from square (6 bits)
        int to = (move >> 6) & 0x3F; //grab to square (6 bits)
        if(from == 0 && to == 0)
            return "<none>"; //noMove placeholder;
        int mov = (move >> 12) & 0x7; //grab moving piece (3bits)
        int cap = (move >> 15) & 0x7; //grab captured piece (3bits)
        int pro = (move >> 18) & 0x7; //grab promotion piece (3bits)
        StringBuilder coordStr = new StringBuilder(); 
        StringBuilder SANStr = new StringBuilder();

        //Add the moving piece
        char movingChar = Piece.uppercase(mov);
		coordStr.append(movingChar);
        coordStr.append(Square.named(from));
        if (movingChar == 'P' && bool(cap)){ //if pawn capture
            SANStr.append(coordStr.toString().charAt(1)); //the file of the pawn
        } else {
            SANStr.append(movingChar);  //the type of piece
        }
        
        //Add 'x' or '-' for capture or noncapture
        //With a null state, we won't see 'x' for en passant captures
        boolean isCapture = bool(cap) || 
        		(movingChar == 'P' && (state != null && to == state.getEnPassantSquare()));
        if (isCapture){
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
            if (movingChar != 'P')
            	throw new IllegalArgumentException(movingChar + ": promotion is only valid for pawns");
            coordStr.append(Piece.uppercase(pro));
            SANStr.append('=');
            SANStr.append(Piece.uppercase(pro));
        }
        return coordStr.toString();
    }
    
    public static int parseUciMove(String uciMove, GameState state)
    {
        /* From UCI specification:        
         * The move format is in long algebraic notation.
         * A nullmove from the Engine to the GUI should be sent as 0000.
         * Examples:  e2e4, e7e5, e1g1 (white short castling), e7e8q (for promotion)
         */
    	if(uciMove == null)
    		throw new NullPointerException("uciMove is null");
    	if(state == null)
    		throw new NullPointerException("state is null");
    	int numMoveChars = uciMove.length();
		if(numMoveChars < 4 || numMoveChars > 5)
    	{
    		throw new IllegalArgumentException(
    				String.format("'%s': must be in long algebraic notation (4 or 5 characters)", uciMove));
    	}
		int fromSquare = Square.squareOf(uciMove.substring(0, 2));
		int toSquare   = Square.squareOf(uciMove.substring(2, 4));
		Piece piece    = state.getPosition().get(fromSquare);
		if(!piece.exists())
			throw new IllegalArgumentException(
					String.format("'%s': no piece exists on %s", uciMove, Square.named(fromSquare)));
		Piece capturedPiece = state.getPosition().get(toSquare);
		Piece promotePiece  = null;
		if(piece.isPawn())
		{
			if(Square.isEighthRank(toSquare, piece.side()))
			{
				if(numMoveChars != 5)
					throw new IllegalArgumentException(
							String.format("'%s': promotion piece not given; add one of [qrnb]", uciMove));
				char promotionChar = uciMove.charAt(4);
				promotePiece = PieceFactory.toPromotePiece(promotionChar);
			} else if (toSquare == state.getEnPassantSquare()) {
				//We need the captured pawn in the encoded move if we ever want to 
				//do EXACT comparisons of this move integer with those given by the move generator
				capturedPiece = state.getPosition().get(Square.squareBehind(toSquare, piece.side()));
				if(!capturedPiece.isPawn())
				{
					String format = "invalid en passant state: ep=%s and %s contains a '%c'";
					String epSquare = Square.named(state.getEnPassantSquare());
					String expectedPawnPlacement = Square.named(Square.squareBehind(toSquare, piece.side()));
					Character actualPiecePlaced = capturedPiece.toChar();
					Object[] args = new Object[]{epSquare, expectedPawnPlacement, actualPiecePlaced};
					throw new IllegalStateException(String.format(format, args));
				}
			}
		}
		return Util.encodeMove(fromSquare, toSquare, piece, capturedPiece, promotePiece);
    }
    
	public static String toUciMove(int move) {
	    int fromSquare = move & 0x3F;
	    int toSquare = (move >> 6) & 0x3F;
	    if(fromSquare == 0 && toSquare == 0)
	        return "<none>"; //or "0000" ???
	    int promotePiece = (move >> 18) & 0x7;
	    String movement = named(fromSquare) + named(toSquare);
	    return bool(promotePiece) ? movement + Piece.lowercase(promotePiece) : movement;
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
        return  formatByteBitmap("piece   : ", (byte)pieceBitmap) + EOL +
                formatByteBitmap("occupied: ", (byte)occupiedBitmap) + EOL +
                formatByteBitmap("attacks : ", (byte)attackBitmap) + EOL;
    }

    public static int encodeMove(int fromSquare, int toSquare, Piece piece,
    		Piece captured, Piece promoter) {
    	if(captured == null) captured = new Empty();
    	if(promoter == null) promoter = new Empty();
    	return EncodeMove(fromSquare, toSquare,	piece.encoded(), captured.encoded(), promoter.encoded());
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

	public static boolean setupState(GameState gameState, String startState) {
	    String position = startState;
	    gameState.set(startState);
	    String initialState = gameState.get();
	    assertEquals(position.substring(0, position.length()-2), initialState.substring(0, position.length()-2));
	    return gameState.isWhiteToMove();
	}
}
