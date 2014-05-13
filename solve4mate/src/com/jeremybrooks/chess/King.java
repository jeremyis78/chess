package com.jeremybrooks.chess;

import static com.jeremybrooks.chess.Bitmap.KING;

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
        long advances = AbstractGenerator.att.king[fromSquare] & notMyPieces;

        //exclude moves that are attacked by opponent's king
        if(position.isKingPlaced(Util.opposing(mySide)))
        {
        	int opposingKingSquare = position.getKingSquare(Util.opposing(mySide));
        	long notAttackedByOpposingKing = ~AbstractGenerator.att.king[opposingKingSquare];
			advances &= notAttackedByOpposingKing;
        }
        return advances;
	}

}