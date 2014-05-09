package com.jeremybrooks.chess;

import static com.jeremybrooks.chess.Bitmap.KNIGHT;

import com.jeremybrooks.chess.Piece.Color;

public class Knight extends Piece {
	
	public Knight(Color color) {
		super(color, KNIGHT, 'N');
	}
	
	@Override
	public boolean exists() {
		return true;
	}
	
	@Override
	public long advances(int fromSquare, Position position)
	{
		int mySide = (color==Color.W?0:1);
		long notMyPieces = ~position.getPieces(mySide);
		return AbstractGenerator.att.knight[fromSquare] & notMyPieces;
	}

}