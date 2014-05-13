package com.jeremybrooks.chess;

import static com.jeremybrooks.chess.Bitmap.QUEEN;

public class Queen extends SlidingPiece {
	
	public Queen(Color color) { 
		super(color, QUEEN, 'Q');
	}

	@Override
	public boolean exists() {
		return true;
	}
}