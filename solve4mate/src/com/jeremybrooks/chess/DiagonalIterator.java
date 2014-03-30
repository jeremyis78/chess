package com.jeremybrooks.chess;

import java.util.Iterator;

public abstract class DiagonalIterator implements Iterator<Integer>
{
	private static final int LENGTH[] = {1,2,3,4,5,6,7,8,7,6,5,4,3,2,1};
	protected int diagonal;
	protected int currentSquare;
	protected int index;

	public DiagonalIterator(int diagonalIndex)
	{
		if(diagonalIndex > LENGTH.length)
			throw new IllegalArgumentException("index must be between in range 0-14");
		diagonal = diagonalIndex;
		currentSquare = startSquare();
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
		return new Integer(currentSquare + (incrementBy() * index++));
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	public abstract int incrementBy();
	
	public abstract int startSquare();

}
