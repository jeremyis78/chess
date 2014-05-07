/**
 * Copyright (C) 2010 Jeremy Brooks. ALL RIGHTS RESERVED.
 * Date: Jan 2, 2010
 */
package com.jeremybrooks.chess;

import static com.jeremybrooks.chess.Bitmap.*;

import java.io.PrintStream;

/**
 * @author jeremy
 *
 */
public class Util {

	private static PrintStream out = System.out;
	private static PrintStream err = System.err;

	

	
	/** 
	 * convenience methods for determining non-zero given a
	 * numerical argument
	 */
    public static boolean bool(int i){ return i != 0; }
    public static boolean bool(long i){ return i != 0; }
	
    public static int opposing(int side){
	    return side == Bitmap.WHITE ? Bitmap.BLACK : Bitmap.WHITE;
	}

    public static int Toggle(int sideToMove){
		if(sideToMove == Bitmap.WHITE)
			return Bitmap.BLACK; 
		return Bitmap.WHITE;	
	}

    public static byte ReverseBits(byte b)
	{
	    byte r = 0;
	    for(int i=0; i<8; i++)
	    {
	        int mask = 1 << i;
	        int bit = (b & mask) >> i;
	        int reversedMask = bit << (7 - i);
	        r |= (byte)reversedMask;
	    }
	    return r;
	}

    public static long ReverseBits(long b){
	    long r = 0;
	    for (int sq=63; sq >= 0; sq--){
	        if (bool((1L << sq) & b)){
	            r |= 1L << (63 - sq);
	        }
	    }
	    return r;
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

    public static int StrToSq(final String str) {
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
//	        	throw new IllegalArgumentException(s + " is an invalid square");
	    } else
	        return NOSQUARE;
//	    	throw new IllegalArgumentException(s + " is an invalid square");
	}
	
    public static String SqToStr(int sq){
	    String s = ""; 
	    if (sq >= Bitmap.A1 && sq <= Bitmap.H8){
	    	s += (char)('a' + (sq % 8));
	    	s += (char)('1' + (sq / 8)); //int division
	    	return s;
	    } 
	    return ""; //throw new IllegalArgumentException(sq + " is an invalid index for a bitboard");
	}

    public static void displaySquares(long b){
		out.println(displaySquaresStr(b));
	}

    public static String displaySquaresStr(long b){
	    StringBuffer sb = new StringBuffer();
		for (int i=0; i<64; i++){
	        if(bool(b & (1L << i))){
	            sb.append(SqToStr(i) + ' ');
	        }
	    }
	    return sb.toString();
	}
	    
    public static int bitCount(long pieces)
    {
    	return Long.bitCount(pieces);
    }
    
// test: sqtoStr(), displayMove(int m), 
    public static char PieceToChar(int color, int piece){
	    char ch = '^'; //should be set to an invalid character
	    switch(piece){
	    case 1: ch = 'P'; break; 
	    case 2: ch = 'N'; break;
	    case 3: ch = 'K'; break;
	    case 5: ch = 'B'; break;
	    case 6: ch = 'R'; break;
	    case 7: ch = 'Q'; break;
	    default: throw new IllegalArgumentException("expecting a piece that's one of (1,2,3,5,6,7), found: " + piece);
	    }
	    if (color == Bitmap.BLACK) 
	        return Character.toLowerCase(ch);
	    else if (color == Bitmap.WHITE)
	    	return ch;
	    throw new IllegalArgumentException("expecting color that's one of {0|1), found: " + color);
	}

    private static String toFenBoard(Position position)
	{
		FenBuilder builder = new FenBuilder();
		builder.appendPieceBoard(position);
		return builder.toString().split(" ")[0];
	}

    private static Position toPosition(String fen)
    {
    	FenParser parser = FenParser.INSTANCE;
    	parser.init(fen);
    	parser.parse();
    	return parser.getPosition();
    }
    
    public static void DisplayBitbrd(long board){
		out.printf("0x%016X", board);
	}

	// displayMoves()
	//
	// Displays the the number of moves up to g.legalMoves[depth]
	// to stdout. Does NOT account for appending '+', '#' for
	// check and checkmate, respectively.
	//
	// PRE: Assumes that g.legalMoves[depth] contains the correct
//	      number of legal moves.

	/* TODO: 1/2/2010 - need this function...add it back
 	//TODO: 1/2/2010 - fix this to accept a gamestate
	void displayMoves(gamestate &g, int moves[], int depth){
	    char delim1 = ' ';  //Delimiter between moves
	    char delim2 = '\n';  //Delimiter after all moves are displayed
	    out.print(g.legalMoves[depth] + "  ");
	    for (int i = 0; i < g.legalMoves[depth]; i++){
		//cout << i << '\t';
	        displayMove(moves[i], false, false);
	        out.print(delim1); //cout << delim1;
	    }
	    out.print(delim2); //cout << delim2;
	}
	*/

    public static void displayMove(int m, boolean check, boolean mate){
		out.print(displayMoveStr(m, check, mate));
	}

    public static String displayMoveStr(int m, boolean check, boolean mate){
	    int from, to, mov, cap, pro;
	    from = m & 0x3F;  //grab from square (6 bits)
	    to = (m >> 6) & 0x3F; //grab to square (6 bits)
	    mov = (m >> 12) & 0x7; //grab moving piece (3bits)
	    cap = (m >> 15) & 0x7; //grab captured piece (3bits)
	    pro = (m >> 18) & 0x7; //grab promotion piece (3bits)

	    char pieceChar[] = {' ','P','N','K',' ','B','R','Q'};
	    StringBuilder coordStr = new StringBuilder(); 
	    StringBuilder SANStr = new StringBuilder();

	    //Add the moving piece
	    coordStr.append(pieceChar[mov]);
	    coordStr.append(Util.SqToStr(from));
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
	    coordStr.append(Util.SqToStr(to));
	    SANStr.append(Util.SqToStr(to));
	    
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
	    if ('K' == pieceChar[mov]){
		    if ((from == Bitmap.E1 && to == Bitmap.G1) || (from == Bitmap.E8 && to == Bitmap.G8)){
	            //printf("%s 0-0", coordStr);
		    	coordStr.append(" 0-0");
		    } else if ((from == Bitmap.E1 && to == Bitmap.C1) || (from == Bitmap.E8 && to == Bitmap.C8)){
		        //printf("%s 0-0-0", coordStr);
		    	coordStr.append(" 0-0-0");
		    } 
	    }
	    //out.printf("0x%06X %s %s ", m, coordStr, SANStr);
	    return coordStr.toString();
	}

    public static void DisplayMoves(long moves){
		out.print(DisplayMovesStr(moves));
	}

    public static String DisplayMovesStr(long moves){
	    long mask = 1L;
	    StringBuffer sb = new StringBuffer();
	    for (int sq = 0; sq < 64; ++sq, mask <<= 1){
	        //If there's a move (aka: a bit set) at this spot, print the move	
	        if (bool(moves & mask)){
	            int file = sq;
	            int rank = 1; //start at first rank
	            for(; file > 7; ++rank)
	                file-=8;
	            //printf("%c%d, ", 'a' + file, rank);
	            sb.append(String.format("%c%d, ", 'a' + file, rank));
	        }
	    }
	    return sb.toString();
	}


	
/* TODO: 1/2/2010 - DO i really need these two functions other than for debugging?	
	void DisplayMoves(unsigned char moves){
	    unsigned char mask = 0x01;
	    int m;
	    for(int i=0; i<8; ++i){
	        m = moves;
	        m &= (mask << i); 
	        if (m > 0){
	            //cout << "\nbefore display_move call: moves = " << i << endl;
	            DisplayMove(i); cout << ", ";
	        }
	    }
	}//end void DisplayMoves(unsigned char moves){
*/

//	void DisplayStatus(unsigned char status){
//	    //The least significant bit of status is b1 (for rank 1)
//	    //so we must left shift it before we use it to display
//	    status = status << 1;
//	    unsigned char mask = 0x01;
//	    for(int i = 0; i < 8; i++, mask <<= 1){
//	        //If there's a bit/move/piece at that square
//	        //Print "X" otherwise a "-"
//	        if (mask & status)
//				out.printf("X ");  
//	        else
//	            out.printf("- ");
//	    }	
//	    out.println();
//	}

    
//    public static void DisplayStatus(int status){
//    	out.print(DisplayStatusStr(status));
//    }
//    
    /* status should be between 0/1 and 255/256  i think */
//   USE formatBitmapByte and shift the argument left one INSTEAD    
//    public static String DisplayStatusStr(int status){
//	    //The least significant bit of status is b1 (for rank 1)
//	    //so we must left shift it before we use it to display
//    	//TODO 1/2/2010 need an assertion test here so we can check for illegal argument
//	    status = status << 1;
//	    short mask = 0x01;
//	    StringBuffer sb = new StringBuffer();
//	    for(int i = 0; i < 8; i++, mask <<= 1){
//	        //If there's a bit/move/piece at that square
//	        //Print "X" otherwise a "-"
//	        if (bool(mask & status))
//				sb.append("X ");  
//	        else
//	            sb.append("- ");
//	    }	
//	    return sb.append("\n").toString();
//	}


    /* these two below are for trouble shooting only */
    public static void DisplayMoves(short moves){
    	out.print(DisplayMovesStr(moves));
    }

    /* this function is designed to print just a single 8 bits of information
     * contained in either one rank or one file on the board.
     */
    public static String DisplayMovesStr(short moves){
    	if (bool(moves & 0xff00)){
    		throw new IllegalArgumentException("moves: " + Integer.toHexString(moves) + " should have only its lower order byte set");
    	}
    	short mask = 1;
	    StringBuffer sb = new StringBuffer();
	    for(int i = 0; i < 8; i++, mask <<= 1){
	        //If there's a bit/move/piece at that square
	        //Print "X" otherwise a "-"
	        if (bool(mask & moves))
				sb.append("X ");  
	        else
	            sb.append("- ");
	    }	
	    return sb.append("\n").toString();
	}

    
    /*
     * Displays a single byte of the bitboard, showing the occupied squares
     * with an 'X' and the piece with a '+'.  Empty squares are denoted with a '-'
     */
    public static String DisplayPieceOnOccupiedRowStr(int piece, short moves){
    	if (bool(piece & 0xffffff00)){
    		throw new IllegalArgumentException("piece: " + Integer.toHexString(piece) + " should have only its lower order byte set");
    	}
    	if (bool(moves & 0xff00)){
    		throw new IllegalArgumentException("moves: " + Integer.toHexString(moves) + " should have only its lower order byte set");
    	}
    	short mask = 1;
	    StringBuffer sb = new StringBuffer();
	    for(int i = 0; i < 8; i++, mask <<= 1){
	        //If there's a bit/move/piece at that square
	        //Print "X" otherwise a "-"
            if (bool(mask & piece)){		//Print the '+' first so
                sb.append("+ ");
	    	} else {
		    	if (bool(mask & moves))
					sb.append("X ");  
		        else
		            sb.append("- ");
            }

	        //do this last so it doesn't get overridden

	    }	
	    return sb.append("\n").toString();
	}

   
    public static void DisplayBoard(long moves){
		out.print(DisplayBoardStr(moves));
	}

    public static String DisplayBoardStr(long moves){
		return DisplayBoardStr(moves, 0L);
	}

    public static void DisplayBoard(long moves, int square){
		out.print(DisplayBoardStr(moves, square));
	}
	
    public static String DisplayBoardStr(long moves, int square){
		return DisplayBoardStr(moves, 1L << square);
	}
	
    public static void DisplayBoard(long moves, long piece){
		out.print(DisplayBoardStr(moves, piece));
	}

    public static String DisplayBoardStr(long moves, long piece){
	    //Displays ASCII chessboard graphic...
	    
	    //This prints the board so that a1 is in the lower
	    //left hand corner and h8 is in the upper right hand
	    //corner--the normal chessboard view.
	    
	    long mask = 1; //, m = 1;
	    
	    int  num_of_sq_to_display = 64; //must be multiple of 8 and <= 64
	    
	    //Above value should be 64 to display the entire chessboard
	    //To display only the first rank (a1-h1) it should be 8.
	    //To display 2nd and 1st rank (a2-h2 and a1-h1) it should be 16, & so on.
	    
	    StringBuffer sb = new StringBuffer();
	    int i, j;
	    for(i = num_of_sq_to_display - 8; i >=0; i-=8){
	        // I cannot manually write "mask = 0x00..01 << i;"
	        // because the compiler treats mask as 32 bits instead of 64 bits
	        // Therefore only "mask = setmask[i];" will work.
	        //	
	        //		mask = setmask[i];  
	        mask = 1L << i;
	        
	        int k = i + 8;  //set upper bound on next for-loop
	        
	        //		printf("%d ", (i / 8) + 1);
	        //cout << ((i/8) + 1) << ' ';
	        sb.append( ((i/8)+1) + " ");
	        for(j = i; j < k; ++j, mask <<= 1){
	            //If there's a bit/move/piece at that square
	            //Print "*" otherwise a "-"
	            
	            if (bool(mask & piece))		//Print the '+' first so
	                sb.append("+ ");  	//we don't overwrite a move
	            else if (bool(mask & moves))
	                sb.append("X ");  
	            else
	                sb.append("- ");
	        }
	        sb.append("\n");
	    }
	    return sb.append("  a b c d e f g h\n").toString();
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
			formatted.append(SqToStr(squareOfPiece));
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

    public static String formatByteBitmap(String header, int bitmap){
    	if (bool(bitmap & 0xffffff00)){
    		throw new IllegalArgumentException(
    				"bitmap should have only bits in its least significant byte set");
    	}
    	return formatByteBitmap(header, (byte) bitmap);
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
     * "piece" row:  	X is the bit set in the least significant byte of pieceBitmap
     * 
     * "occupied" row: 	the X's are the other pieces (bits set) in the least sig. 
     * 					byte of occupied bitmap
     * 
     * "attacks" row: 	the X's indicate the possible moves (bits set) in movesBitmap   
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
		return 	formatByteBitmap("piece   : ", (byte)pieceBitmap) + "\n" +
				formatByteBitmap("occupied: ", (byte)occupiedBitmap) + "\n" +
				formatByteBitmap("attacks : ", (byte)attackBitmap) + "\n";
    }

    
    /**
     * Status is shorthand way of representing the occupied squares (bits)
     * on a rank (bitmap).  The status represents the middle six bits of a byte.
     * We can use iterate over all possible statuses by looping from [1..63]
     * Since the last piece on either end of a rank is always attacked whether
     * there is a bit there or not we use status as a way to save on memory usage.
     * Example,
     * 
     * 	TODO: give examples
     * 
     * @param status a bitmap representing the occupied state on the given rank/file/diag
     * @return
     */
    public static String formatStatus(byte status){
	    //The least significant bit of status is b1 (for rank 1)
	    //so we must left shift it before we use it to display
    	//TODO 1/2/2010 need an assertion test here so we can check for illegal argument
	    status = (byte)(status << 1);
	    return formatByteBitmap(status);
	}


    public static String formatStatus(int status){
    	if (bool(status & 0xffffff00)){
    		throw new IllegalArgumentException(
    				"bitmap should have only bits in its least significant byte set");
    	}
    	return formatStatus((byte)status);
    }



    /**
     * 	Displays a human readable ASCII chess board graphic showing
     *  the bit set in singleBitBitmap with a '+' and the bits set in
     *  multipleBitsBitmap with an 'X'.  The singleBitBitmap must contain
     *  on a single bit set. This is mainly for debugging purposes. It's 
     *  useful for printing a human readable form of, for example, the 
     *  placement of a Queen and all the squares she attacks.
     *     The board is printed so that a1 is in the lower left hand corner
     *  and h8 is in the upper right hand corner--the normal chess board view.
	 *  
	 *  The two bitmaps should not overlap (singleBitBitmap & multipleBitBitmap == 0)
	 *
     * @param singleBitBitmap a bitmap containing a single bit set
     * @param multipleBitsBitmap a bitmap containing any number of 
     * 			bits set
     * @return an ASCII chess board graphic depicting the set bits
     */
    public static String formatLongBitmapAsBoard(long singleBitBitmap, long multipleBitsBitmap){

    	if (bitCount(singleBitBitmap) > 1){
    		throw new IllegalArgumentException("singleBitBitmap: " + Long.toBinaryString(singleBitBitmap) + " can have only a single bit set");
    	}
    	if (bool(singleBitBitmap & multipleBitsBitmap)){
    		throw new IllegalArgumentException("singleBitBitmap and multipleBitsBitmap have overlapping bits. This is probably not desired.");
    	}
	    
	    long mask = 1; //, m = 1;
	    
	    int  num_of_sq_to_display = 64; //must be multiple of 8 and <= 64
	    
	    //Above value should be 64 to display the entire chess board
	    //To display only the first rank (a1-h1) it should be 8.
	    //To display 2nd and 1st rank (a2-h2 and a1-h1) it should be 16, & so on.
	    
	    StringBuffer sb = new StringBuffer();
	    int i, j;
	    for(i = num_of_sq_to_display - 8; i >=0; i-=8){
	        mask = 1L << i;
	        
	        int k = i + 8;  //set upper bound on next for-loop
	        
	        //printf("%d ", (i / 8) + 1);
	        //cout << ((i/8) + 1) << ' ';
	        sb.append( ((i/8)+1) + " ");
	        for(j = i; j < k; ++j, mask <<= 1){
	            //If there's a bit/move/piece at that square
	            //Print "*" otherwise a "-"
	            
	            if (bool(mask & singleBitBitmap))		//Print the '+' first so
	                sb.append("+ ");  	//we don't overwrite a move
	            else if (bool(mask & multipleBitsBitmap))
	                sb.append("X ");  
	            else
	                sb.append("- ");
	        }
	        sb.deleteCharAt(sb.toString().length()-1); //delete last space
	        sb.append("\n");
	    }
	    return sb.append("  a b c d e f g h\n").toString();
	}

    /**
     * Displays a chess board with X's for any bits set in bitmap
     */
    public static String formatLongBitmapAsBoard(long bitmap){ 
    	return formatLongBitmapAsBoard(0x0, bitmap);
    }
    
    
    public static String formatSideBySide(String left, String right)
    {
    	StringBuffer sb = new StringBuffer();
    	String[] leftSide = left.split("\n");
    	String[] rightSide = right.split("\n");
    	if (leftSide.length != rightSide.length)
    	{
    		throw new IllegalArgumentException(
    				"left and right args must have same num of lines");
    	}
    	for(int i=0; i < leftSide.length; i++)
    	{
    		sb.append(leftSide[i] + " " + rightSide[i] + "\n");
    	}
    	return sb.toString();
    }
	// minusOneRank
	//
	// Returns the from square given the square
	// the pawn moved to. (pawn advanced one square)
	//
	protected static int squareBehind(int currentSquare, int side){
	    return (side == WHITE) ? (currentSquare - 8) : (currentSquare + 8);
	}
	
	// minusTwoRank
	//
	// Returns the from square given the square
	// the pawn moved to. (pawn advanced two squares)
	//
	protected static int twoSquaresBehind(int currentSquare, int side){
		return squareBehind(squareBehind(currentSquare, side), side);
	}

	protected static int squareAhead(int currentSquare, int side){
	    return (side == WHITE) ? (currentSquare + 8) : (currentSquare - 8);
	}

	protected static int squareLeftOf(int currentSquare){
	    return squareLeftOf(currentSquare, 0);
	}

	@Deprecated
	protected static int squareLeftOf(int currentSquare, int side){
	    return (currentSquare - 1);
	}

	protected static int squareRightOf(int currentSquare){
	    return squareRightOf(currentSquare, 0);
	}
	
	@Deprecated
	protected static int squareRightOf(int currentSquare, int side){
	    return (currentSquare + 1);
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

}
