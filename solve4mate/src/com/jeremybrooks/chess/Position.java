package com.jeremybrooks.chess;

import static com.jeremybrooks.chess.Bitmap.*;

import java.io.PrintStream;

/**                                                                 
 *  A position represents the physical locations of all pieces on
 *  the chessboard.
 * 
 * @author jeremy
 *
 */

public class Position
{
	private static final String EMPTY_BOARD = "8/8/8/8/8/8/8/8";
	private static PrintStream out = System.out;
	private static final int KING_NOT_PLACED = -1;

    private long pieces[][] = new long[Color.MAXCOLOR][Pieces.MAXPIECE];
    long all[] = new long[MAXALL];
    int board[] = new int[64];
    int kingSq[] = new int[]{KING_NOT_PLACED, KING_NOT_PLACED};

	public Position(){
		set(EMPTY_BOARD);
	}
	
	public Position(final String FEN_Board){
		set(FEN_Board);
	}
	
	public long getWhitePawns() {
		return pieces[Color.WHITE][Pieces.PAWNS];
	}

	public long getWhiteKnights() {
		return pieces[Color.WHITE][Pieces.KNIGHTS];
	}

	public long getWhiteBishops() {
		return pieces[Color.WHITE][Pieces.BISHOPS];
	}

	public long getWhiteRooks() {
		return pieces[Color.WHITE][Pieces.ROOKS];
	}

	public long getWhiteQueens() {
		return pieces[Color.WHITE][Pieces.QUEENS];
	}

	public long getWhiteKing() {
		if(isKingPlaced(Color.WHITE)) {
	    	return 1L << kingSq[Color.WHITE];
	    }
	    return 0L;
	}

	public long getBlackPawns() {
		return pieces[Color.BLACK][Pieces.PAWNS];
	}

	public long getBlackKnights() {
		return pieces[Color.BLACK][Pieces.KNIGHTS];
	}

	public long getBlackBishops() {
		return pieces[Color.BLACK][Pieces.BISHOPS];
	}

	public long getBlackRooks() {
		return pieces[Color.BLACK][Pieces.ROOKS];
	}

	public long getBlackQueens() {
		return pieces[Color.BLACK][Pieces.QUEENS];
	}
	
	public long getBlackKing() {
		if(isKingPlaced(Color.BLACK)) {
	    	return 1L << kingSq[Color.BLACK];
	    }
	    return 0L;
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
	
	public long getOpponentPiecesExceptKing(int color)
	{
		int opponentColor = (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
		return pieces[opponentColor][Pieces.ALLPIECES];
	}

	private boolean isNotTheKing(int p) {
		return p <= Pieces.QUEEN;
	}

	private boolean isKingPlaced(int side) {
		return kingSq[side] != KING_NOT_PLACED;
	}
	
	public int getBoard(int square)
	{
		return board[square];
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
	    kingSq[Color.WHITE] = KING_NOT_PLACED;
	    kingSq[Color.BLACK] = KING_NOT_PLACED;
	    for (int i = Color.WHITE; i <= Color.BLACK; i++){
	        pieces[i][Pieces.PAWNS] = 0L;
	        pieces[i][Pieces.KNIGHTS] = 0L;
	        pieces[i][Pieces.BISHOPS] = 0L;
	        pieces[i][Pieces.ROOKS] = 0L;
	        pieces[i][Pieces.QUEENS] = 0L;
	        pieces[i][Pieces.ALLPIECES] = 0L;
	    }
	    all[ALL] = 0L;
	    all[ALL90] = 0L;
	    all[ALL45L] = 0L;
	    all[ALL45R] = 0L;
	    
	    for (int i = A1; i <= H8; i++)
	    {
	    	board[i] = BOARD_EMPTY_SQUARE; 
	    }
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
		                {
		                    throw new IllegalArgumentException("board has too many white kings");
		                }
		                all[ALL] |= sqMask;
		                all[ALL90] |= sqMask90;
		                all[ALL45L] |= sqMask45L;
		                all[ALL45R] |= sqMask45R;
		                board[sq] = PIECE[Bitmap.KING];
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
		                {
		                    throw new IllegalArgumentException("board has too many black kings");
		                }
		                all[ALL] |= sqMask;
		                all[ALL90] |= sqMask90;
		                all[ALL45L] |= sqMask45L;
		                all[ALL45R] |= sqMask45R;
		                board[sq] = -PIECE[Bitmap.KING];
		                kingSq[Color.BLACK] = sq;
		                isKingPlaced[Color.BLACK] = true;
		                break;
		            default: //illegal character
		                throw new IllegalArgumentException("board contains invalid piece '" + c + "'"); 
		            }
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
	        if (i < A8){
	            fen[fenIndex++] = '/'; //Rank separator only on first 7..not the last one
	        }
	        for (int j = i; j < i+8; j++){
	            switch(board[j]){
	            //white pieces: pawns, knights, bishops, rooks, queens, king
	            case 1:
	                if (contEmptySquares > 0){
	                    fen[fenIndex++] = toChar(contEmptySquares); 
	                    contEmptySquares = 0;
	                }
	                fen[fenIndex++] = BOARD_PIECE[0];
	                break;
	            case 2:
	                if (contEmptySquares > 0){
	                    fen[fenIndex++] = toChar(contEmptySquares);
	                    contEmptySquares = 0;
	                }
	                fen[fenIndex++] = BOARD_PIECE[1];
	                break;
	            case 5:
	                if (contEmptySquares > 0){
	                    fen[fenIndex++] = toChar(contEmptySquares); 
	                    contEmptySquares = 0;
	                }
	                fen[fenIndex++] = BOARD_PIECE[2];
	                break;
	            case 6:
	                if (contEmptySquares > 0){
	                    fen[fenIndex++] = toChar(contEmptySquares); 
	                    contEmptySquares = 0;
	                }
					fen[fenIndex++] = BOARD_PIECE[3];
					break;
	            case 7:
	                if (contEmptySquares > 0){
	                    fen[fenIndex++] = toChar(contEmptySquares); 
	                    contEmptySquares = 0;
	                }
	                fen[fenIndex++] = BOARD_PIECE[4];
	                break;
	            case 3:
	                if (contEmptySquares > 0){
	                    fen[fenIndex++] = toChar(contEmptySquares); 
	                    contEmptySquares = 0;
	                }
	                fen[fenIndex++] = BOARD_PIECE[5];
	                break;
	                
	            //black pieces: pawns, knights, bishops, rooks, queens, king
	            case -1:
	                if (contEmptySquares > 0){
	                    fen[fenIndex++] = toChar(contEmptySquares); 
	                    contEmptySquares = 0;
	                }
					fen[fenIndex++] = BOARD_PIECE[6];
					break;
	            case -2:
	                if (contEmptySquares > 0){
	                    fen[fenIndex++] = toChar(contEmptySquares); 
	                    contEmptySquares = 0;
	                }
	                fen[fenIndex++] = BOARD_PIECE[7];
	                break;
	            case -5:
	                if (contEmptySquares > 0){
	                    fen[fenIndex++] = toChar(contEmptySquares); 
	                    contEmptySquares = 0;
	                }
	                fen[fenIndex++] = BOARD_PIECE[8];
	                break;
	            case -6:
	                if (contEmptySquares > 0){
	                    fen[fenIndex++] = toChar(contEmptySquares); 
	                    contEmptySquares = 0;
	                }
	                fen[fenIndex++] = BOARD_PIECE[9];
	                break;
	            case -7:
	                if (contEmptySquares > 0){
	                    fen[fenIndex++] = toChar(contEmptySquares); 
	                    contEmptySquares = 0;
	                }
	                fen[fenIndex++] = BOARD_PIECE[10];
	                break;
	            case -3:
	                if (contEmptySquares > 0){
	                    fen[fenIndex++] = toChar(contEmptySquares); 
	                    contEmptySquares = 0;
	                }
	                fen[fenIndex++] = BOARD_PIECE[11];
	                break;
	            default:
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
		out.print(new BitboardDisplayer().formatBoard(this));
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
	    all[Bitmap.ALL] ^= mask;
	    all[ALL90] ^= 1L << SQ2BIT90R[sq];
	    all[ALL45L] ^= 1L << SQ2BIT45L[sq];
	    all[ALL45R] ^= 1L << SQ2BIT45R[sq];
	    board[sq] = BOARD_EMPTY_SQUARE;
	    if (p == Pieces.KING)
	    {
	    	kingSq[c] = KING_NOT_PLACED;
	    }  else {
	        pieces[c][p] ^= mask;
	        pieces[c][Pieces.ALLPIECES] ^= mask;
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
