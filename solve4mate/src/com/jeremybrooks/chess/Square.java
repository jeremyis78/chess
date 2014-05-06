package com.jeremybrooks.chess;


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
}
