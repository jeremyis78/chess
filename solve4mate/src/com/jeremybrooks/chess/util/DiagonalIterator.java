package com.jeremybrooks.chess.util;

import java.util.Iterator;

/**
 * <p>Implementors will define how to iterate over the squares found
 * within a diagonal on the chess board.
 * 
 * <p>Thirty diagonals are found on the board.  Half of the digaonals
 * are associated with diagonals found on a bitboard rotated 45
 * degrees left and the other half on a bitboard rotated 45 degrees
 * right. Implementors of this class will define those left and
 * right iterators by implementing {@code startSquare} and
 * {@code nextSquareOffset}
 * 
 * <p>For example to iterate over the squares (that is, the bit indexes) of a bitboard's
 * third diagonal (zero-indexed) you would do the following:
 * 
 * <pre><code>
 * DiagonalIterator di =  new ConcreteImplementation(2)
 * while(di.hasNext())
 * {
 * 	 int currentSquareIndex = di.next();
 *   //use the index to access that bit within the bitboard
 * }
 * </code>
 * </pre>
 * 
 * @author jeremy
 *
 */
public abstract class DiagonalIterator implements Iterator<Integer>
{
	private static final int LENGTH[] = {1,2,3,4,5,6,7,8,7,6,5,4,3,2,1};
	protected int diagonal;
	protected int index;

	public DiagonalIterator(int diagonalIndex)
	{
		if(diagonalIndex > LENGTH.length)
			throw new IllegalArgumentException("index '"+diagonalIndex+"' must be in range 0-14");
		diagonal = diagonalIndex;
		index = 0;
	}

	public int diagonalLength()
	{
		return LENGTH[diagonal];
	}

	@Override
	public boolean hasNext() {
		return index < diagonalLength();
	}

	@Override
	public Integer next() {
		return new Integer(startSquare() + (numberOfNextSquares() * nextSquareOffset())); //G34 and/or G6
	}

	private int numberOfNextSquares() {  //G20
		return index++;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Gets the square (the index) of the first square on this diagonal
	 */
	public abstract int startSquare();
	
	/**
	 * Gets the offset to proceed to the next square in the diagonal
	 */
	public abstract int nextSquareOffset();
	
}
