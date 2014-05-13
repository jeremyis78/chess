package com.jeremybrooks.chess;

import static com.jeremybrooks.chess.Bitmap.ROOK;

public class Rook extends SlidingPiece {
	
	public Rook(Color color) { 
		super(color, ROOK, 'R');
	}

	@Override
	public boolean exists() {
		return true;
	}
}