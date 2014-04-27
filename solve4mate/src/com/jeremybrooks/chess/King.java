package com.jeremybrooks.chess;

import static com.jeremybrooks.chess.Bitmap.KING;

import com.jeremybrooks.chess.Piece.Color;

public class King extends Piece {
	private static final Attacks att = MoveGenerator.att;

	public King(Color color) 
	{ 
		super(color, KING, 'K');
	}

	@Override
	public boolean exists() {
		return true;
	}
	/* 
	 * Assumes that we have a "colored" piece so we can grab the squares the 
	 * opposing king is attacking to be sure we don't move there
	 */
	@Override
	public long nonCaptures(int fromSquare, Position position) {
		int side = (color==Color.W?0:1);
		long allPiecesByRank = position.getAllPieces(0);
		long emptySquares = ~allPiecesByRank;

        long pMoves = att.king[fromSquare] & emptySquares; // & ~att.king[position.getKingSquare(Util.opposing(side))];

        //exclude moves that are attacked by opponent's king
        pMoves &= ~att.king[position.getKingSquare(Util.opposing(side))];
        return pMoves;
	}

}