package com.jeremybrooks.chess;

import static com.jeremybrooks.chess.Bitmap.*;

public class Pawn extends Piece {
	
	public Pawn(Color color){ 
		super(color, PAWN, 'P');
	}

	@Override
	public boolean exists() {
		return true;
	}
	
	@Override
	public long advances(int fromSquare, Position position) {
		int side = (color==Color.W?WHITE:BLACK);
		long occupiedSquares = position.getAllPieces(0);
		long emptySquares = ~occupiedSquares;
		long pawnsMoveOne = 0;
		if (side == Bitmap.WHITE) {
			long whitePawns = position.getPawns(Bitmap.WHITE);
			pawnsMoveOne = (whitePawns << 8) & emptySquares;
		} else {
			long blackPawns = position.getPawns(Bitmap.BLACK);
			pawnsMoveOne = (blackPawns >> 8) & emptySquares;
		}
		return pawnsMoveOne;
	}

	@Override
	public long attacks(int fromSquare, Position position) {
		long attacks = 0L;
		int side = (color==Color.W?0:1);
		attacks = AbstractGenerator.att.pawn[side][fromSquare];
		return attacks;
	}

}