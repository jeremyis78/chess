package com.jeremybrooks.chess;

public class LeftDiagonalIterator extends DiagonalIterator {

	private static final int START_SQUARE[] = {0,1,2,3,4,5,6,7,15,23,31,39,47,55,63};

	public LeftDiagonalIterator(int diagonalIndex) {
		super(diagonalIndex);
	}

	@Override
	public int incrementBy() {
		return 7;
	}

	@Override
	public int startSquare() {
		return START_SQUARE[diagonal];
	}

}
