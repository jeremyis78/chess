package com.jeremybrooks.chess;

import static com.jeremybrooks.chess.Bitmap.BISHOP;

import com.jeremybrooks.chess.Piece.Color;

public class Bishop extends SlidingPiece {

	public Bishop(Color color) { 
		super(color, BISHOP, 'B');
	}

	@Override
	public boolean exists() {
		return true;
	}
}