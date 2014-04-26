package com.jeremybrooks.chess;


/**
 * <pre>
 * Represents a square on the chess board.
 * 
 * A Square has one of two states: 
 *      1) empty/unoccupied, 
 *              {@code square.isOccupied() == true}
 *      2) occupied by a piece,
 *              {@code square.isOccupied() == false}
 * </pre>
 * @author jeremy
 *
 */
public class Square {
	private static final Square EMPTY_SQUARE = new Square();
	
	private Piece piece;
	
	/**
	 * Constructs an unoccupied square
	 */
	public Square() {
		super();
	}
	
	/**
	 * Constructs a square containing the given piece
	 * 
	 * @param piece the piece on this square
	 */
	public Square(Piece piece) {
		super();
		this.piece = piece;
	}

	public boolean isOccupied()
	{
		return (piece != null);
	}

	public void clear()
	{
		piece = null;
	}

	public Piece getPiece()
	{
		if (piece == null)
			return Piece.absent();
		return piece;
	}

	public void setPiece(Piece piece)
	{
		this.piece = piece;
	}
	
	public static Square with(Piece piece)
	{
		return new Square(piece);
	}

	public static Square unoccupied()
	{
		return EMPTY_SQUARE;
	}

	public static Square fromBoard(int boardPiece)
	{
		Piece piece = Piece.fromBoardPiece(boardPiece);
		if(piece.exists())
		{
			return Square.with(piece);
		}
		return Square.unoccupied();
	}

}
