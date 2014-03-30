package com.jeremybrooks.chess;

public class RightDiagonalIterator extends DiagonalIterator {

	private static final int START_SQUARE[] = {7,6,5,4,3,2,1,0,8,16,24,32,40,48,56};
	
	public RightDiagonalIterator(int diagonalIndex) {
		super(diagonalIndex);
	}

	@Override
	public int incrementBy() {
		return 9;
	}

	@Override
	public int startSquare() {
		return START_SQUARE[diagonal];
	}

}
