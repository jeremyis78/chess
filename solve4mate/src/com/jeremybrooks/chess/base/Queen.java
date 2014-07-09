package com.jeremybrooks.chess.base;

import static com.jeremybrooks.chess.base.Bitmap.QUEEN;

public class Queen extends SlidingPiece {
	
	public Queen(Color color) { 
		super(color, QUEEN, 'Q');
	}

	@Override
	public boolean exists() {
		return true;
	}
}