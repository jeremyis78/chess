package com.jeremybrooks.chess;

import static com.jeremybrooks.chess.Bitmap.*;

import java.io.PrintStream;

/*******************************************************************/
/*	                       position.cpp                            */
/*******************************************************************/
/*                                                                 */
/*  This file contains the implementation for the position class.  */
/*  A position represents the physical locations of all pieces on  */
/*  the chessboard.                                                */
/*                                                                 */
/*******************************************************************/

//Declare a constant brd object for using attacks and masks
//in the position class
//const brd att;

public class Position
{
	private static PrintStream out = System.out;

//	private static final long PIECE_START[][] = {
//	{	/* W pawns   */ 0x000000000000FF00L,  //bitbrd(0xFF00),	
//		/* W knights */ 0x0000000000000042L,  //bitbrd(0x42),
//		/* W bishops */ 0x0000000000000024L,  //bitbrd(0x24),
//		/* W rooks   */ 0x0000000000000081L,  //bitbrd(0x81),
//		/* W queen   */ 0x0000000000000010L,  //bitbrd(0x10),
//		/* W pieces  */ 0x000000000000FFFFL   //bitbrd(0xFFFF),
//	},
//	{	/* B pawns   */ 0x00FF000000000000L,  //bitbrd(0xFF) << 56;
//		/* B knights */ 0x4200000000000000L,  //bitbrd(0x42) << 56;
//		/* B bishops */ 0x2400000000000000L,  //bitbrd(0x24) << 56;
//		/* B rooks   */ 0x8100000000000000L,  //bitbrd(0x81) << 56;
//		/* B queen   */ 0x0800000000000000L,  //bitbrd(0x08) << 56;
//		/* B pieces  */ 0xFFFF000000000000L   //bitbrd(0xFFFF) << 48;
//	}
//	};
//	
//	private static final long ALL_START[] = {
//		/*  all     */ 0xFFFF00000000FFFFL,	
//		/*  all90   */ 0xC3C3C3C3C3C3C3C3L,
//		/*  all45L  */ 0xFB31861C38618CDFL,
//		/*  all45R  */ 0xFB31861C38618CDFL
//	};
	
	private static final long EDGES = 0xFF818181818181FFL;
	private static final int KING_NOT_PLACED = -1;

	//TODO: 1/9/2010 - Add a bitmap in the pieces array for the king
	//and remove the kingSq[] array and w/bKingSq variables
	//TODO: 1/9/2010 - Remove the board[] array and calculate what the board
	//looks like on the fly instead.
    long pieces[][] = new long[Color.MAXCOLOR][Pieces.MAXPIECE];
    long all[] = new long[MAXALL];
    int board[] = new int[64];
    int kingSq[] = new int[]{KING_NOT_PLACED, KING_NOT_PLACED};
	
	public Position(){
	}
	
	public Position(final String FEN_Board){
		set(FEN_Board);
	}
	
	//Return a bitbrd of the pieces whose color is 'side'
	// and piece is 'p' 
	//
	public long getPieces(int side, int piece){
	    if (isNotTheKing(piece)) {
	        return pieces[side][piece];
	    } else if(isKingPlaced(side)) {
	    	return 1L << kingSq[side];
	    }
	    return 0L;
	}

	private boolean isNotTheKing(int p) {
		return p <= Pieces.QUEEN;
	}

	private boolean isKingPlaced(int side) {
		return kingSq[side] != KING_NOT_PLACED;
	}
	
	public int getBoard(int sq)
	{
		return board[sq];
	}
	
	
	/*******************************************************************/
	/*              The bits of an EncodedMove                         */
	/*******************************************************************/
	/* +--------+--------+---------+----------+-----------+---------+  */
	/* | 0 - 5  | 6 - 11 | 12 - 14 | 15 - 17  |  18 - 20  | 21 - 31 |  */
	/* +--------+--------+---------+----------+-----------+---------+  */
	/* |  from  |   to   | moving  | captured | promotion | unused  |  */
	/* | square | square | piece   | piece    |   piece   |         |  */
	/* +--------+--------+---------+----------+-----------+---------+  */
	/*                                                                 */
	/*******************************************************************/
		
	public void clear(){
	    kingSq[Color.WHITE] = kingSq[Color.BLACK] = KING_NOT_PLACED;
	    
	    for (int i = Color.WHITE; i <= Color.BLACK; i++){
	        pieces[i][Pieces.PAWNS] = pieces[i][Pieces.KNIGHTS] = 0L;
	        pieces[i][Pieces.BISHOPS] = pieces[i][Pieces.ROOKS] = 0L;
	        pieces[i][Pieces.QUEENS] = pieces[i][Pieces.ALLPIECES] = 0L;
	    }
	    all[ALL] = all[ALL90] = all[ALL45L] = all[ALL45R] = 0L;
	    
	    for (int i = A1; i <= H8; i++)
	        board[i] = BOARD_EMPTY_SQUARE; 
	    
	    //Assign a sentinal value for the board's edges
	    //	for (int i = 0; i < 28; i++)
	    //		board[BOARD_EDGES[i]] = BOARD_EDGE_SQUARE; 
	}
	
	static void validateFiles(String rankFen)
	{
		int len = rankFen.length();
		if (len == 0 || len > 8)
		{
			throw new IllegalArgumentException("fen must contain eight squares on a rank, found " + rankFen);
		}
		
		int filesRead = 0;
		for(int i=0; i < len; i++)
		{
			char c = rankFen.charAt(i);
			if (Character.isDigit(c))
			{
				filesRead += c - '0'; //represents multiple files
				continue;
			}
			filesRead++; //represents a single file
		}
		if(filesRead != 8)
		{
			throw new IllegalArgumentException("pieces and empty squares on rank do not fit on eight files");
		}
	}
	
	public boolean set(final String fen)
	{
		boolean[] isKingPlaced = new boolean[] {false, false};
				
		String[] ranks = fen.split("/");
//		out.println("ranks.length = " + ranks.length);
		if (ranks.length != 8)
		{
			throw new IllegalArgumentException("fen must contain eight ranks");
		}
		//Start with the first rank
		int sq = Bitmap.A1; //keep track of the square we're on
		for(int m = ranks.length - 1; m >= 0; m--)
		{
			String rank = ranks[m];
			validateFiles(rank);
			for (int n = 0; n < rank.length(); n++)
			{
				char c = rank.charAt(n);
				if (Character.isDigit(c))
				{
					int emptysq = sq;
		            //add digit's value to sq
		            sq += c - '0'; 
	            
		            //initialize those squares to empty
		            while (emptysq < sq)
		                board[emptysq++] = BOARD_EMPTY_SQUARE;
				}
				else
				{
		            //Get the appropriate bitboard masks
		            long sqMask = 1L << sq;
		            long sqMask90 = 1L << SQ2BIT90R[sq];
		            long sqMask45L = 1L << SQ2BIT45L[sq];
		            long sqMask45R = 1L << SQ2BIT45R[sq];
		            
		            switch(c){
		            case 'P':
		                placePiece(Color.WHITE, PAWN, sq);
		                break;
		            case 'N':
		                placePiece(Color.WHITE, Pieces.KNIGHT, sq);
		                break;
		            case 'B':
		                placePiece(Color.WHITE, Pieces.BISHOP, sq);
		                break;
		            case 'R':
		                placePiece(Color.WHITE, Pieces.ROOK, sq);
		                break;
		            case 'Q':
		                placePiece(Color.WHITE, Pieces.QUEEN, sq);
		                break;
		            case 'K':
		                if(isKingPlaced[Color.WHITE])
		                { //if(kingSq[Color.WHITE] != -1){
		                    throw new IllegalArgumentException("board has too many white kings");
		                }
		                all[ALL] |= sqMask;
		                all[ALL90] |= sqMask90;
		                all[ALL45L] |= sqMask45L;
		                all[ALL45R] |= sqMask45R;
		                board[sq] = PIECE[Bitmap.KING];
		                //wKingSq = sq;
		                kingSq[Color.WHITE] = sq;
		                isKingPlaced[Color.WHITE] = true;
		                break;
		            case 'p':
		                placePiece(Color.BLACK, PAWN, sq);
		                break;
		            case 'n':
		                placePiece(Color.BLACK, Pieces.KNIGHT, sq);
		                break;
		            case 'b':
		                placePiece(Color.BLACK, Pieces.BISHOP, sq);
		                break;
		            case 'r':
		                placePiece(Color.BLACK, Pieces.ROOK, sq);
		                break;
		            case 'q':
		                placePiece(Color.BLACK, Pieces.QUEEN, sq);
		                break;
		            case 'k':
		                if(isKingPlaced[Color.BLACK])
		                {//if(kingSq[Color.BLACK] != -1){
		                    throw new IllegalArgumentException("board has too many black kings");
		                }
		                all[ALL] |= sqMask;
		                all[ALL90] |= sqMask90;
		                all[ALL45L] |= sqMask45L;
		                all[ALL45R] |= sqMask45R;
		                board[sq] = -PIECE[Bitmap.KING];
		                //bKingSq = sq;
		                kingSq[Color.BLACK] = sq;
		                isKingPlaced[Color.BLACK] = true;
		                break;
		            default: //illegal character
		                throw new IllegalArgumentException("board contains invalid piece '" + c + "'"); 
		            }//end switch(c)
		            sq++;
		            //files++;
				}
				
			}
		}
		
		if( eitherKingIsMissing(isKingPlaced) )
		{
			throw new IllegalArgumentException("board is missing one or both kings");
		}
	    
	    if(areKingsAdjacent()){
	        throw new IllegalArgumentException("board cannot have adjacent kings");
	    }

		return true;
	}

	private boolean areKingsAdjacent() {
		return Util.adjacentSquares(kingSq[Color.WHITE], kingSq[Color.BLACK]);
	}

	private boolean eitherKingIsMissing(boolean[] isKingPlaced) {
		return !(isKingPlaced[Color.WHITE] && isKingPlaced[Color.BLACK]);
	}
	
	
	
	boolean isLegal(int move, int sideToMove){
	    //Determines whether or not a move is legal
	    //for 'sideToMove' player.
	    //For now just makes sure the king is not exposed
	    //to attack. (TODO: or is left in check )
	    //Call this function after each move to determine
	    //its legality.
	    //PRECONDITION: Any squares in the board[] array
	    //              that are along the edge of the board
	    //              that is a1-h1, h1-h8, a1-a8, or a8-h8
	    //              must be either a piece or BOARD_EDGE
	    //              It MUST be this way for this algorithm to work
	
	    //Algorithm:
	    //      Starting with the king square find the first
	    //    piece in each of the 8 directions (do them
	    //    individually).  If that piece is an enemy piece
	    //    see if it can attack along that kind of 
	    //    ray (rank/file or diagonal). If it can
	    //    then the move is illegal.
	    //      Finally, pretend the king is a knight
	    //    and examine the squares that the knight would attack
	    //    and see if an enemy knight is sitting there.  If so,
	    //    the move is illegal.  Then check for pawns checking the king.
	    //    Return true otherwise.
	    //    
	
	    int from, to;
//	    int cap;  1/9/2010 not used in this function
	    boolean legal = true;  //return value initialized to a legal move
	    int bq = 0x01;  //bishop or queen
	    int rq = 0x02;  //rook or queen
	    int dir[] = new int[]{+7, +8, +9, +1, -7, -8, -9, -1};  //directional increment
	    int dirPiece[] = new int[]{bq, rq, bq, rq, bq, rq, bq, rq};
	    int attacker;
	    int kingSquare;
	   
	    from = move & 0x3F;         //first six bits
	    to = (move >> 6) & 0x3F;    //next six bits   
//	    cap = (move >> 15) & 0x7;   //captured piece
	
	    //move the piece
	    board[to] = board[from];
	
	    //remove it from the old square 
	    if (Util.bool(EDGES & (1L << from)))
	        board[from] = BOARD_EDGE_SQUARE;
	    else
	        board[from] = BOARD_EMPTY_SQUARE;   //remove the moving piece
	
	    // just to compile comment out below line
	    kingSquare = kingSq[sideToMove];
	
	    // See if a knight is checking the king
	
	    //if (att.knight[kingSq] & pieces[!sideToMove][KNIGHTS])
	    //    legal = 0;
	
	    // Now check to see if king has been exposed to check
	    // Exit loop as soon as we find it isn't a legal move
	
	    for (int i = 0; legal && i < 8; i++){
	        int j; 
	        for (j = kingSquare; board[j] != BOARD_EDGE_SQUARE; j += dir[i]){
	            
	            // If the attacker and the king are on the same ray
	            // and the attacker is NOT on the board's edge
	            // see if it can attack the king along that ray
	
	            if (!isEmpty(board[j]) && !isSameColor(sideToMove, board[j])){
					attacker = Math.abs(board[j]);
		            switch(attacker){
		                case Pieces.BISHOP:
		                case Pieces.ROOK:
		                case Pieces.QUEEN:
		                    if (Util.bool(attacker & dirPiece[i]))
		                        legal = false;
		                    break;
		                default: //any other piece, still legal
		                    break;
		            }
				}
	        }
	
	        // If the attacker and the king are on the same ray
	        // and the attacker IS on the board's edge
	        // see if it can attack the king along that ray
	
	        if (!isEmpty(board[j]) && !isSameColor(sideToMove, board[j])){
				attacker = Math.abs(board[j]);
	            switch(attacker){
	                case Pieces.BISHOP:
	                case Pieces.ROOK:
	                case Pieces.QUEEN:
	                    if (Util.bool(attacker & dirPiece[i]))
	                        legal = false;
	                    break;
	                default: //any other piece, still legal
	                    break;
	            }
			}
	    } //end for i        
	
	    return legal;
	}
	
	
	void Display(){
		DisplayBoard();
		DisplayBitboardBoard();
	}
	
	
	void displayFEN(){
	    System.out.print(getFen());
	}
	
	public String getFen()
	{
	    char fen[] = new char[100];
	    int fenIndex = 0;
	    int contEmptySquares = 0;
	    for (int i = A8; i >= A1; i-=8){
	        //cout << i/8 + 1 << " | "; //Print the rank number
	        if (i < A8){
	            fen[fenIndex++] = '/'; //Rank separator only on first 7..not the last one
	        }
	        for (int j = i; j < i+8; j++){
	            switch(board[j]){
	                //white pieces: pawns, knights, bishops, rooks, queens, king
	            case 1: //cout << BOARD_PIECE[0] << ' ';
	                if (contEmptySquares > 0){
	                    fen[fenIndex++] = toChar(contEmptySquares); 
	                    contEmptySquares = 0;
	                }
	                fen[fenIndex++] = BOARD_PIECE[0];
	                break;
	            case 2: //cout << BOARD_PIECE[1] << ' ';
	                if (contEmptySquares > 0){
	                    fen[fenIndex++] = toChar(contEmptySquares);
	                    contEmptySquares = 0;
	                }
	                fen[fenIndex++] = BOARD_PIECE[1];
	                break;
	            case 5: //cout << BOARD_PIECE[2] << ' ';
	                if (contEmptySquares > 0){
	                    fen[fenIndex++] = toChar(contEmptySquares); 
	                    contEmptySquares = 0;
	                }
	                fen[fenIndex++] = BOARD_PIECE[2];
	                break;
	            case 6: //cout << BOARD_PIECE[3] << ' ';
	                if (contEmptySquares > 0){
	                    fen[fenIndex++] = toChar(contEmptySquares); 
	                    //sb.append(contEmptySquares);
	                    contEmptySquares = 0;
	                }
					fen[fenIndex++] = BOARD_PIECE[3];
					break;
	            case 7: //cout << BOARD_PIECE[4] << ' ';
	                if (contEmptySquares > 0){
	                    fen[fenIndex++] = toChar(contEmptySquares); 
	                    contEmptySquares = 0;
	                }
	                fen[fenIndex++] = BOARD_PIECE[4];
	                break;
	            case 3: //cout << BOARD_PIECE[5] << ' ';
	                if (contEmptySquares > 0){
	                    fen[fenIndex++] = toChar(contEmptySquares); 
	                    contEmptySquares = 0;
	                }
	                fen[fenIndex++] = BOARD_PIECE[5];
	                break;
	                
	                //black pieces: pawns, knights, bishops, rooks, queens, king
	            case -1: //cout << BOARD_PIECE[6] << ' ';
	                if (contEmptySquares > 0){
	                    fen[fenIndex++] = toChar(contEmptySquares); 
	                    contEmptySquares = 0;
	                }
					fen[fenIndex++] = BOARD_PIECE[6];
					break;
	            case -2: //cout << BOARD_PIECE[7] << ' ';
	                if (contEmptySquares > 0){
	                    fen[fenIndex++] = toChar(contEmptySquares); 
	                    contEmptySquares = 0;
	                }
	                fen[fenIndex++] = BOARD_PIECE[7];
	                break;
	            case -5: //cout << BOARD_PIECE[8] << ' ';
	                if (contEmptySquares > 0){
	                    fen[fenIndex++] = toChar(contEmptySquares); 
	                    contEmptySquares = 0;
	                }
	                fen[fenIndex++] = BOARD_PIECE[8];
	                break;
	            case -6: //cout << BOARD_PIECE[9] << ' ';
	                if (contEmptySquares > 0){
	                    fen[fenIndex++] = toChar(contEmptySquares); 
	                    contEmptySquares = 0;
	                }
	                fen[fenIndex++] = BOARD_PIECE[9];
	                break;
	            case -7: //cout << BOARD_PIECE[10] << ' ';
	                if (contEmptySquares > 0){
	                    fen[fenIndex++] = toChar(contEmptySquares); 
	                    contEmptySquares = 0;
	                }
	                fen[fenIndex++] = BOARD_PIECE[10];
	                break;
	            case -3: //cout << BOARD_PIECE[11] << ' ';
	                if (contEmptySquares > 0){
	                    fen[fenIndex++] = toChar(contEmptySquares); 
	                    contEmptySquares = 0;
	                }
	                fen[fenIndex++] = BOARD_PIECE[11];
	                break;
	            default: //cout << "- ";
	                //increment a counter for contiguous empty squares
	                contEmptySquares++;
	                break;
	            }
	        }
	        //tack on any empty squares at the end
	        if (contEmptySquares > 0 && i >= A1){
	            fen[fenIndex++] = toChar(contEmptySquares);
	            contEmptySquares = 0;
	        }	
	    }
	    return new String(fen).trim();
	}

	/**
	 * Converts the integer i to a char
	 * 
	 * @param i
	 * @return
	 */
	private char toChar(int i) {
		if (i < 0 || i > 9)
		{
			throw new IllegalArgumentException("i should be in range [0..9], found " + i);
		}
		return new Integer(i).toString().charAt(0);
	}
	
	public void DisplayBoard()
	{
		out.print(new Displayer().formatBoard(this));
	}
	
	void DisplayBitboardBoard()
	{
		out.print(new Displayer().formatAllBitboards(this));
	}
	
	/**
	 * Places a piece on the board and in the bitmaps
	 * 
	 * @param c the color of the piece as in Color.WHITE/BLACK
	 * @param p the index into the PIECE[] array Chess.PAWN, etc
	 * @param sq the square to place the piece on
	 */
	public void placePiece(int c, int p, int sq){
	    long mask = 1L << sq;
	    all[ALL] |= mask;
	    all[ALL90] |= 1L << SQ2BIT90R[sq];
	    all[ALL45L] |= 1L << SQ2BIT45L[sq];
	    all[ALL45R] |= 1L << SQ2BIT45R[sq];
	    if (c == Color.WHITE){
	      board[sq] = PIECE[p];
	    } else {
	      board[sq] = -PIECE[p];
	    }
	
	    if (p == Pieces.KING){
	            kingSq[c] = sq;
	    } else {
	        pieces[c][p] |= mask;
	        pieces[c][Pieces.ALLPIECES] |= mask;
	    }
	}
	
	/**
	 * Erases a piece from the board and bitmaps
	 * 
	 * @param c the color of the piece as in Color.WHITE/BLACK
	 * @param p the index into the PIECE[] array Chess.PAWN, etc
	 * @param sq the square to place the piece on
	 */
	public void erasePiece(int c, int p, int sq){
	    long mask = 1L << sq;
	    all[ALL] ^= mask;
	    all[ALL90] ^= 1L << SQ2BIT90R[sq];
	    all[ALL45L] ^= 1L << SQ2BIT45L[sq];
	    all[ALL45R] ^= 1L << SQ2BIT45R[sq];
	    board[sq] = BOARD_EMPTY_SQUARE;
	
	    if (p != Pieces.KING){
	        pieces[c][p] ^= mask;
	        pieces[c][Pieces.ALLPIECES] ^= mask;
	    } 
	
	    //    if (p == Pieces.KING){
	    //  kingSq[c] = -1; 
	    //  //we are assuming that if we erase a king we will
	    //  //place another somewhere else with placePiece()
	    //} else {
	    //    pieces[c][p] ^= mask;
	    //    pieces[c][ALLPIECES] ^= mask;
	    //} 
	}
	
	
	void movePiece(int c, int p, int cap, int pro, int from, int to){
	    long maskTo, maskFr;
	    maskTo = 1L << to;
	    maskFr = 1L << from;
	    board[from] = BOARD_EMPTY_SQUARE;
	    if (p == Bitmap.KING){
	        kingSq[c] = to;
	    } else {
	        //Remove non-king piece on 'from' square(XOR-ASSIGN "^=")
	        pieces[c][p] ^= maskFr;
	        pieces[c][Pieces.ALLPIECES] ^= maskFr;
	
	        //Add non-king piece on 'to' square (OR-ASSIGN "|=")
		if (pro == NONE) {
		    pieces[c][p] |= maskTo;
		} else {
		    pieces[c][pro] |= maskTo;
		}
	        pieces[c][Pieces.ALLPIECES] |= maskTo;
	    }
	
	    //Remove the piece at square 'from' (to be run for all pieces
	    //including the king)
	    all[ALL] ^= maskFr;
	    all[ALL90] ^= 1L << SQ2BIT90R[from];
	    all[ALL45L] ^= 1L << SQ2BIT45L[from];
	    all[ALL45R] ^= 1L << SQ2BIT45R[from];
	
	    //Add the piece at square 'to'(all pieces including king)
	    all[ALL] |= maskTo;
	    all[ALL90] |= 1L << SQ2BIT90R[to];
	    all[ALL45L] |= 1L << SQ2BIT45L[to];
	    all[ALL45R] |= 1L << SQ2BIT45R[to];
		
	    //Remove the captured piece
	    //Update the board array with either the piece or the promotion piece
	     switch(c){
	     case Color.WHITE:
	         if (cap != NONE)
	             erasePiece(Color.BLACK, cap, to);
	
	         if (pro == NONE){
	             board[to] = PIECE[p];
	         } else {
	             board[to] = PIECE[pro];
	         }
	         break;
	     case Color.BLACK:
	         if (cap != NONE)
	             erasePiece(Color.WHITE, cap, to);
	         if (pro == NONE){
	             board[to] = -PIECE[p];
	         } else {
	             board[to] = -PIECE[pro];
	         }
	         break;
	     }
	}
	
	public static boolean isSameColor(int c, int p)
	{
		if ( (p > 0 && c == Color.WHITE) || (p < 0 && c == Color.BLACK) )
	            return true;
		return false;
	}
	
	public static boolean isEmpty(int p)
	{
		return (p == BOARD_EMPTY_SQUARE);
	}
}
