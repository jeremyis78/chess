package com.jeremybrooks.chess.base;

import static com.jeremybrooks.chess.base.Bitmap.*;
/**
 * <pre>
 * Represents a piece on the chess board.
 * 
 * TODO: Work to eventually replace Position's "int board[64]" to be an array of these
 * instead (which would eliminate the static {@link #fromBoardPiece(int)} method.
 * 
 * Eventually (and ideally) we should be able massage this class into something that
 * can be used in the move generators (for even more complexity reduction)
 * </pre>
 * @author jeremy
 *
 */
/*
 * Of the 37 classes as of this commit, these are the only classes remaining
 * with a complexity of 4 or greater.
 * 
 * class                  Line coverage  Branch coverage   Complexity
 * ---------------------- -------------  ---------------   ----------
	EscapeGenerator	        85% 79/92      66% 42/63        33
	NonCaptureGenerator	   100% 81/81      93% 41/44        27
	CaptureGenerator	   100% 54/54      96% 28/29        19
	Search++                 0% 0/83        0% 0/38         6.5
	SolveForMate++	         0% 0/46        0% 0/22         5.5
	MoveGenerator	        85% 206/242    72% 112/154      4.483
	Evaluator	            79%	54/68      70% 21/30        4.4
	 ++ = currently has NO unit tests
 *
 */
public abstract class Piece {

	public static enum Color{W, B}

	protected Color color;
	protected int index;
	protected char displayCh;
	
	public Piece()
	{
		super();
	}

	public Piece(Color pieceColor, int pieceIndex, char displayCharacter)
	{
		super();
		this.color = pieceColor;
		this.index = pieceIndex;
		this.displayCh = displayCharacter;
	}

	public char toChar()
	{ 
		return color == Color.W 
			? displayCh 
			: Character.toLowerCase(displayCh);
	}

	public String toString() { return ""+toChar(); }

	/**
	 * Get the value used for indexing into the bitboard arrays in 
	 * Position.getPieces(int color, int piece)
	 * @return the index of this piece 
	 */
	public int index() { return index; }
	
	/**
	 * Gets the piece encoded for or retrieved from a 'move' int.
	 * The encoded value only represents the type of piece; the color is not encoded.
	 * 
	 * @return the encoded piece
	 */
	public int encoded() { return PIECE[index()]; }
	
	/**
	 * Gets the piece as would be returned from {@link #encoded()} but 
	 * if the piece is Black it will return {@code -1 * moveEncoded()}
	 * If the piece has no color or there is no piece then 
	 * {@code Absent.toChar()} is returned.
	 * @return the piece's color encoded value or Absent.toChar()
	 */
	public int encodedByColor() 
	{
		if(!exists())
			return BOARD_EMPTY_SQUARE;
		return (color == Color.W ? encoded() : -1 * encoded());
	}

	/**
	 * Should return false ONLY when this piece represents
	 * an absent piece (ie, no piece at all)
	 * 
	 * @return true if this refers to a piece, false if empty/absent
	 */
	public abstract boolean exists();
	
	/**
	 * Get a bitboard containing all squares where this piece can advance to
	 * excluding captures.
	 * All moves that indicate a pseudo legal move for this piece (e.g. pawn 
	 * advance.
	 * 
	 * @param fromSquare  square on which the piece resides, or NOSQUARE if moves are generated en masse 
	 * @param position  the current position on the board
	 * @return
	 */
	public abstract long advances(int fromSquare, Position position);
	
	/**
	 * Get a bitboard containing all squares attacked given the piece sits on fromSquare.
	 * Attacked squares include those occupied by friendly (non-enemy) pieces, a.k.a. squares
	 * of defended pieces. 
	 * 
	 * @param fromSquare The square on which the piece sits.
	 * @param position The current board position
	 * @return
	 */
	public abstract long attacks(int fromSquare, Position position);
	
}