package com.jeremybrooks.chess;

import static com.jeremybrooks.chess.Bitmap.PIECE_STR;

public class BitboardDisplayer extends AbstractDisplayer {


	public void appendPiece(Position position, int currentSquare) {
		long maskForCurrentSquare = 1L << currentSquare;
		//If there's a piece at that square
		//Print PIECE[c][p] otherwise print "-"
		boolean nopiece = true;
		for (int c = Color.WHITE; c <= Color.BLACK; c++){
		    for (int p = 0; p <= Pieces.QUEENS; p++){ 
		        if (Util.bool(maskForCurrentSquare & position.getPieces(c, p))){
		            display.append(PIECE_STR[c][p]);  
		            nopiece = false;
		                }
		    }
		}
		if (nopiece){
		    display.append("-"); //print "- " for empty square
		}
	}

}
