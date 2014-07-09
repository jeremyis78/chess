package com.jeremybrooks.chess.base;

import static com.jeremybrooks.chess.base.Bitmap.KING;

import com.jeremybrooks.chess.movegen.Attacks;
import com.jeremybrooks.chess.util.Util;

public class King extends Piece {

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
	public long advances(int fromSquare, Position position) {
		int mySide = (color==Color.W?0:1);
		long notMyPieces = ~position.getPieces(mySide);
        long advances = attacks(fromSquare, position) & notMyPieces;

        //exclude moves that are attacked by opponent's king
        if(position.isKingPlaced(Util.opposing(mySide)))
        {
        	int opposingKingSquare = position.getKingSquare(Util.opposing(mySide));
        	long notAttackedByOpposingKing = ~Attacks.forPiece(this, opposingKingSquare, position);
			advances &= notAttackedByOpposingKing;
        }
        return advances;
	}

	@Override
	public long attacks(int fromSquare, Position position) {
		long attacks = Attacks.forPiece(this, fromSquare, position);
		return attacks;
	}

}