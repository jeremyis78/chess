package com.jeremybrooks.chess;

import static com.jeremybrooks.chess.Bitmap.*;

public class Empty extends Piece {

	public Empty()
	{
		super(Piece.Color.W, NONE, BOARD_EMPTY_SQUARE);
	}
	
	@Override
	public boolean exists() {
		return false;
	}

	@Override
	public long advances(int fromSquare, Position position) {
		return 0;
	}

	@Override
	public long attacks(int fromSquare, Position position) {
		return 0;
	}

}
