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
	public long nonCaptures(int fromSquare, Position position) {
		// TODO Auto-generated method stub
		return 0;
	}

}
