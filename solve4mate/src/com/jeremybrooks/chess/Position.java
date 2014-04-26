package com.jeremybrooks.chess;

import static com.jeremybrooks.chess.Bitmap.*;

import java.io.PrintStream;
import java.util.Arrays;

import com.jeremybrooks.chess.Piece.Color;

/**                                                                 
 *  A position represents the physical locations of all pieces on
 *  the chessboard.
 *
 *  TODO: Pieces!!!!! (that's the best name for this class which would
 *  be used within a Board (formerly GameState) class
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
    private long all[] = new long[MAXALL];
    private Square board[] = new Square[64];
    private int kingSq[] = new int[]{KING_NOT_PLACED, KING_NOT_PLACED};

	public Position(){
		clear();
	}
	
	public long getPawns(int side)
	{
		return pieces[side][PAWNS];		
	}

	public long getOpponentPawns(int side)
	{
		return pieces[Util.opposing(side)][PAWNS];		
	}
	
	public long getKnights(int side)
	{
		return pieces[side][KNIGHTS];		
	}
	
	public long getOpponentKnights(int side)
	{
		return pieces[Util.opposing(side)][KNIGHTS];		
	}

	public long getBishops(int side)
	{
		return pieces[side][BISHOPS];		
	}

	public long getOpponentBishops(int side)
	{
		return pieces[Util.opposing(side)][BISHOPS];		
	}

	public long getRooks(int side)
	{
		return pieces[side][ROOKS];		
	}

	public long getOpponentRooks(int side)
	{
		return pieces[Util.opposing(side)][ROOKS];		
	}

	public long getQueens(int side)
	{
		return pieces[side][QUEENS];		
	}

	public long getOpponentQueens(int side)
	{
		return pieces[Util.opposing(side)][QUEENS];		
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
		int opponentSide = Util.opposing(side); 
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

    public long getAllPieces(int rotationInDegrees)
    {
    	switch(rotationInDegrees)
    	{
    	case -45:
    		return all[ALL45L];
    	case 0:
    		return all[ALL];
    	case 45:
    		return all[ALL45R];
    	case 90:
    		return all[ALL90];
    	default:
    		throw new IllegalArgumentException("invalid rotation "+rotationInDegrees+"; rotation must be -45, 0, 45 or 90");
    	}
    }

	private boolean isNotTheKing(int p) {
		return p <= QUEEN;
	}

	public boolean isKingPlaced(int side) {
		return kingSq[side] != KING_NOT_PLACED;
	}
	
	public int getBoard(int square)
	{
		return board[square].getPiece().encodedByColor();
	}

	public boolean isNotEmpty(int square)
	{
		return !isEmpty(square);
	}
	
	public boolean isEmpty(int square)
	{
		return !(board[square].isOccupied()); // == BOARD_EMPTY_SQUARE);
	}

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
	    	board[i] = new Square(); 
	    }
	}	

	/**
	 * Places a piece on the board and in the bitmaps
	 * 
	 * @param c the color of the piece as in Color.WHITE/BLACK
	 * @param p the index into the PIECE[] array Chess.PAWN, etc
	 * @param sq the square to place the piece on
	 */
	public void placePiece(int c, int p, int sq){
		if(p == KING && kingSq[c] != KING_NOT_PLACED)
		{
			throw new IllegalStateException("cannot place " + (c==WHITE?"white":"black") +
					" king on "+ Util.SqToStr(sq)+"; already placed on " +
					Util.SqToStr(kingSq[c])+ "; use erasePiece()");
		}
		if(isNotEmpty(sq))
		{
			throw new IllegalStateException(Util.SqToStr(sq)+ " is already occupied");
		}
//		System.out.println("placing "+(c==WHITE?"white":"black")+" "+p+" on " +Util.SqToStr(sq));
	    long mask = 1L << sq;
	    all[ALL] |= mask;
	    all[ALL90] |= 1L << SQ2BIT90R[sq];
	    all[ALL45L] |= 1L << SQ2BIT45L[sq];
	    all[ALL45R] |= 1L << SQ2BIT45R[sq];
	    if (c == Bitmap.WHITE){
	      board[sq].setPiece(Piece.fromConstant(Color.W, p));   // = PIECE[p]; //white piece
	    } else {
	    	board[sq].setPiece(Piece.fromConstant(Color.B, p)); // = -PIECE[p];
	    }
	
	    if (p == KING){
	            kingSq[c] = sq;
	    } else {
	        pieces[c][p] |= mask;
	        pieces[c][ALLPIECES] |= mask;
	    }
	}
	
	/**
	 * Erases/removes the piece on the given square
	 * 
	 * @param sq the square of the piece to remove
	 */
	public void erasePiece(int square){
		int boardPiece = getBoard(square);
		if(boardPiece == BOARD_EMPTY_SQUARE)
		{
			return; //already empty
		}
		int color = (boardPiece > 0) ? WHITE : BLACK;
		int piece = TO_PIECE[Math.abs(boardPiece)];
//		System.out.println("erasing "+(color==WHITE?"white":"black")+" piece on " + Util.SqToStr(square));
	    long mask = 1L << square;
	    all[Bitmap.ALL] ^= mask;
	    all[ALL90] ^= 1L << SQ2BIT90R[square];
	    all[ALL45L] ^= 1L << SQ2BIT45L[square];
	    all[ALL45R] ^= 1L << SQ2BIT45R[square];
	    board[square].clear();
	    if (piece == KING)
	    {
	    	kingSq[color] = KING_NOT_PLACED;
	    }  else {
	        pieces[color][piece] ^= mask;
	        pieces[color][ALLPIECES] ^= mask;
	    } 
	}

	public static boolean isSameColor(int c, int p)
	{
		if ( (p > 0 && c == Bitmap.WHITE) || (p < 0 && c == Bitmap.BLACK) )
	            return true;
		return false;
	}
	
}
