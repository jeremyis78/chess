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
	
	private static final int PAWNS = 0;
	private static final int KNIGHTS = 1;
	private static final int BISHOPS = 2;
	private static final int ROOKS = 3;
	private static final int QUEENS = 4;
	private static final int ALLPIECES = 5;

	private static final String EMPTY_BOARD = "8/8/8/8/8/8/8/8";
	private static PrintStream out = System.out;
	private static final int KING_NOT_PLACED = -1;

    private long pieces[][] = new long[2][6];
    long all[] = new long[MAXALL];
    int board[] = new int[64];
    private int kingSq[] = new int[]{KING_NOT_PLACED, KING_NOT_PLACED};

	public Position(){
		set(EMPTY_BOARD);
	}
	
	public Position(final String FEN_Board){
		set(FEN_Board);
	}
	
	public long getPawns(int side)
	{
		return pieces[side][PAWNS];		
	}

	public long getOpponentPawns(int side)
	{
		return pieces[Util.opp(side)][PAWNS];		
	}
	
	public long getKnights(int side)
	{
		return pieces[side][KNIGHTS];		
	}
	
	public long getOpponentKnights(int side)
	{
		return pieces[Util.opp(side)][KNIGHTS];		
	}

	public long getBishops(int side)
	{
		return pieces[side][BISHOPS];		
	}

	public long getOpponentBishops(int side)
	{
		return pieces[Util.opp(side)][BISHOPS];		
	}

	public long getRooks(int side)
	{
		return pieces[side][ROOKS];		
	}

	public long getOpponentRooks(int side)
	{
		return pieces[Util.opp(side)][ROOKS];		
	}

	public long getQueens(int side)
	{
		return pieces[side][QUEENS];		
	}

	public long getOpponentQueens(int side)
	{
		return pieces[Util.opp(side)][QUEENS];		
	}
	
	public long getKing(int side)
	{
		if(isKingPlaced(side)) {
	    	return 1L << kingSq[side];
	    }
	    return 0L;
	}

	public long getOpponentKing(int side)
	{
		int opponentSide = (side == Bitmap.WHITE) ? Bitmap.BLACK : Bitmap.WHITE; 
		if(isKingPlaced(opponentSide)) {
	    	return 1L << kingSq[opponentSide];
	    }
	    return 0L;
	}

	public int getKingSquare(int side) {
		return kingSq[side];
	}

	public long getPieces(int side, int piece){
	    if (isNotTheKing(piece)) {
	        return pieces[side][piece];
	    } else if(isKingPlaced(side)) {
	    	return 1L << kingSq[side];
	    }
	    return 0L;
	}

	public void setPieces(int side, int piece, long bitmap) {
		int squareOfPiece = 0;
		while(bitmap != 0)
		{
			squareOfPiece = Bitmap.lowestBitNumber(bitmap);
			placePiece(side, piece, squareOfPiece);
			bitmap = Bitmap.clearBit(bitmap, squareOfPiece);
		}
	}

	public long getOpponentPiecesExceptKing(int color)
	{
		int opponentColor = (color == Bitmap.WHITE) ? Bitmap.BLACK : Bitmap.WHITE;
		return pieces[opponentColor][ALLPIECES];
	}

	private boolean isNotTheKing(int p) {
		return p <= QUEEN;
	}

	public boolean isKingPlaced(int side) {
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
	    kingSq[Bitmap.WHITE] = KING_NOT_PLACED;
	    kingSq[Bitmap.BLACK] = KING_NOT_PLACED;
	    for (int i = Bitmap.WHITE; i <= Bitmap.BLACK; i++){
	        pieces[i][PAWNS] = 0L;
	        pieces[i][KNIGHTS] = 0L;
	        pieces[i][BISHOPS] = 0L;
	        pieces[i][ROOKS] = 0L;
	        pieces[i][QUEENS] = 0L;
	        pieces[i][ALLPIECES] = 0L;
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
		                placePiece(Bitmap.WHITE, PAWN, sq);
		                break;
		            case 'N':
		                placePiece(Bitmap.WHITE, KNIGHT, sq);
		                break;
		            case 'B':
		                placePiece(Bitmap.WHITE, BISHOP, sq);
		                break;
		            case 'R':
		                placePiece(Bitmap.WHITE, ROOK, sq);
		                break;
		            case 'Q':
		                placePiece(Bitmap.WHITE, QUEEN, sq);
		                break;
		            case 'K':
		                if(isKingPlaced[Bitmap.WHITE])
		                {
		                    throw new IllegalArgumentException("board has too many white kings");
		                }
		                all[ALL] |= sqMask;
		                all[ALL90] |= sqMask90;
		                all[ALL45L] |= sqMask45L;
		                all[ALL45R] |= sqMask45R;
		                board[sq] = PIECE[Bitmap.KING];
		                kingSq[Bitmap.WHITE] = sq;
		                isKingPlaced[Bitmap.WHITE] = true;
		                break;
		            case 'p':
		                placePiece(Bitmap.BLACK, PAWN, sq);
		                break;
		            case 'n':
		                placePiece(Bitmap.BLACK, KNIGHT, sq);
		                break;
		            case 'b':
		                placePiece(Bitmap.BLACK, BISHOP, sq);
		                break;
		            case 'r':
		                placePiece(Bitmap.BLACK, ROOK, sq);
		                break;
		            case 'q':
		                placePiece(Bitmap.BLACK, QUEEN, sq);
		                break;
		            case 'k':
		                if(isKingPlaced[Bitmap.BLACK])
		                {
		                    throw new IllegalArgumentException("board has too many black kings");
		                }
		                all[ALL] |= sqMask;
		                all[ALL90] |= sqMask90;
		                all[ALL45L] |= sqMask45L;
		                all[ALL45R] |= sqMask45R;
		                board[sq] = -PIECE[Bitmap.KING];
		                kingSq[Bitmap.BLACK] = sq;
		                isKingPlaced[Bitmap.BLACK] = true;
		                break;
		            default: //illegal character
		                throw new IllegalArgumentException("board contains invalid piece '" + c + "'"); 
		            }
		            sq++;
		            //files++;
				}
				
			}
		}
		return true;
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
	    if (c == Bitmap.WHITE){
	      board[sq] = PIECE[p];
	    } else {
	      board[sq] = -PIECE[p];
	    }
	
	    if (p == KING){
	            kingSq[c] = sq;
	    } else {
	        pieces[c][p] |= mask;
	        pieces[c][ALLPIECES] |= mask;
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
	    if (p == KING)
	    {
	    	kingSq[c] = KING_NOT_PLACED;
	    }  else {
	        pieces[c][p] ^= mask;
	        pieces[c][ALLPIECES] ^= mask;
	    } 
	}
	
	public static boolean isSameColor(int c, int p)
	{
		if ( (p > 0 && c == Bitmap.WHITE) || (p < 0 && c == Bitmap.BLACK) )
	            return true;
		return false;
	}
	
	public static boolean isEmpty(int p)
	{
		return (p == BOARD_EMPTY_SQUARE);
	}


}
