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
	public long nonCaptures(int fromSquare, Position position)
	{
		long allPiecesByRank = position.getAllPieces(0);
		long emptySquares = ~allPiecesByRank;
		return MoveGenerator.att.knight[fromSquare] & emptySquares;
	}

}